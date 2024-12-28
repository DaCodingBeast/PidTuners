package CommonUtilities

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Arm.AngleRange
import android.util.Log
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
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
data class PIDFParams(val kp: Double, val ki: Double, val kd: Double, val kf: Double)

/**
 * PIDF controller
 * @param params PIDF coefficients
 * @see PIDFParams
 */

class PIDFcontroller(
    private var params: PIDFParams,
    private val motor: Motor? = null,
    private val obstacleRange: AngleRange? = null, val angleOffset: Double? = null
) {

    private var prevError = 0.0
    private var integral = 0.0
    private var target: AngleRange? = null
    private val armAngle =
        if (motor != null && angleOffset != null) ArmAngle(motor, angleOffset) else null

    class Result(val motorPower: Double, val error: Double)

    /**
     * @param angleRange Used to determine the feedforward term to fight gravity
     * @param error Error determined by the motor direction
     * @see AngleRange.findPIDFAngleError
     * @see AngleRange.findMotorDirection
     * @return A motor power that is in the range of 1 to -1
     */
    fun calculate(
        angleRange: AngleRange,
        badAngleRange: AngleRange?,
        dt: Double = Dt
    ): Result {

        val direction = AngleRange.findMotorDirection(angleRange, badAngleRange)
        val error = AngleRange.findPIDFAngleError(direction, angleRange)

        /**
         * A way to prevent integral windup.
         * It only applies the integral term when the motor power is being dampened
         */
        integral += (error * dt)

        val derivative = (error - prevError) / dt
//        println(derivative)
        prevError = error

        //TODO BOOOOOOOOOOOOOOOOM
        val ff = if(angleRange.start>0 ) max(0.0, sin(angleRange.start)) * params.kf else min(0.0, sin(angleRange.start)) * params.kf

//        println(" d: ${derivative * params.kd}  i: ${integral * params.ki}   f: $ff   p: ${error*params.kp} angle: ${Math.toDegrees(angleRange.start)}")
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
    fun resetConstantsAndTarget(params: PIDFParams, target: AngleRange) {
        require(motor != null) { Log.d(ArmSpecific.error,"You did not instantiate the PIDF controller with the your motor type") }
        this.params = params
        this.target = target
    }

    /**
     * This function should be after reset, needs to access a non null Angle Range
     */
    fun calculateMotorPower(encoder: Int, looptime: Double): Double {
        val angleRange = AngleRange(armAngle!!.findAngle(encoder), target!!.stop)
        return calculate(angleRange, obstacleRange, looptime).motorPower
    }

    /**
     * Check if Angle Target has been relatively reached, so user can change their own custom states
     * @param degreeAccuracy Angle Accuracy for system to return true In Degrees
     */

    fun targetReached(encoder: Int, degreeAccuracy: Double = 5.0): Boolean{
        val angleRange = AngleRange(armAngle!!.findAngle(encoder), target!!.stop)
        val direction  = AngleRange.findMotorDirection(angleRange, obstacleRange)
        return (abs(AngleRange.findPIDFAngleError(direction, angleRange)) < Math.toRadians(degreeAccuracy))
    }

}





