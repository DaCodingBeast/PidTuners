package CommonUtilities

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Algorithm.Vector
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.Simulators.Target
import com.qualcomm.robotcore.util.ElapsedTime
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
class PIDParams(val kp: Double, val ki: Double, val kd: Double, val kf: Double = 0.0) {
    constructor(params: Vector) : this(
        params.particleParams[0],
        params.particleParams[1],
        params.particleParams[2],
        params.particleParams.getOrNull(3) ?: 0.0
    )
}


/**
 * PIDF controller
 * @param params PIDF coefficients
 * @see PIDParams
 */
class Result(val motorPower: Double, val error: Double)

class PIDFcontroller(var params: PIDParams, val isSimulator: Boolean) {

    private var prevError = 0.0
    private var integral = 0.0

    // Pre-calculated constants
    private val dtInverse = 1.0 / Dt
    private val hasFF = params.kf != 0.0

    private lateinit var timer: ElapsedTime

    init {
        if (!isSimulator) timer = ElapsedTime()
    }

    private fun getInverseLoopTime(): Double{
        return if (isSimulator){
            dtInverse
        }else{
            1/ (timer.seconds())
        }
    }

    fun calculate(position: Target, obstacle: Target?): Result {

        when (position) {
            is AngleRange -> {
                val (_, error) = AngleRange.findDirectionAndError(position, obstacle as AngleRange?)

                val ff = if (hasFF) {
                    val sinVal = sin(position.start)
                    if (position.start > 0.0) max(0.0, sinVal) * params.kf
                    else min(0.0, sinVal) * params.kf
                } else 0.0

                return calculateControl(error, ff)
            }

            is SlideRange -> {
                val error = position.stop - position.start
                return calculateControl(error, 0.0)
            }
        }
    }

    private inline fun calculateControl(error: Double, ff: Double): Result {
        integral += error * getInverseLoopTime()
        val derivative = (error - prevError) * getInverseLoopTime()
        prevError = error

        val controlEffort = (error * params.kp + integral * params.ki + derivative * params.kd + ff)
            .coerceIn(-1.0, 1.0)

        if(!isSimulator) timer.reset()

        return Result(controlEffort, error)
    }

    fun reset() {
        prevError = 0.0
        integral = 0.0
    }
}