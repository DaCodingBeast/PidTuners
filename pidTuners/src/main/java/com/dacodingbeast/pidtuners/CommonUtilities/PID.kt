package CommonUtilities

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import android.util.Log
import com.dacodingbeast.pidtuners.Algorithm.Vector
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.dacodingbeast.pidtuners.Simulators.Target
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideRange
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * PIDF coefficients
 * @param kp Proportional Term
 * @param ki Integral Term
 * @param kd Derivative Term
 * @param kf FeedForward Term, used for fighting gravity forces based on angle
 */
class PIDParams (val kp: Double, val ki: Double, val kd: Double, val kf: Double = 0.0){
    constructor(params: Vector): this(
        params.particleParams[0],
        params.particleParams[1],
        params.particleParams[2],
        params.particleParams.getOrNull(3)?: 0.0
    )
}


/**
 * PIDF controller
 * @param params PIDF coefficients
 * @see PIDParams
 */

class PIDFcontroller(
    var params: PIDParams,
    private val motor: Motor? = null,
    private val obstacleRange: AngleRange? = null, val angleOffset: Double? = null
) {

    private var prevError = 0.0
    private var integral = 0.0
    private var target: AngleRange? = null
    class Result(val motorPower: Double, val error: Double)

    /**
     * @param angleRange Used to determine the feedforward term to fight gravity
     * @param error Error determined by the motor direction
     * @see AngleRange.findPIDFAngleError
     * @see AngleRange.findMotorDirection
     * @return A motor power that is in the range of 1 to -1
     */
    fun calculate(
        position: Target,
    ): Result {

        var ff=0.0
        val error = when(position){
            is AngleRange -> {
                val direction = AngleRange.findMotorDirection(position, )
                ff = if(position.start>0 ) max(0.0, sin(position.start)) * params.kf else min(0.0, sin(position.start)) * params.kf
                AngleRange.findPIDFAngleError(direction, position)
            }
            is SlideRange ->{
                position.stop - position.start
            }

        }

        integral += (error * Dt)

        val derivative = (error - prevError) / Dt
        prevError = error

        val controlEffort =
            ((derivative * params.kd + integral * params.ki + error * params.kp) + ff).coerceIn(
                -1.0,
                1.0
            )
        return Result(controlEffort, error)
    }

    /**
     * This is for using the PIDF params in an opmode
     */
    fun resetConstantsAndTarget(params: PIDParams, target: AngleRange) {
        require(motor != null) { Log.d(ArmSpecific.error,"You did not instantiate the PIDF controller with the your motor type") }
        this.params = params
        this.target = target
    }
}