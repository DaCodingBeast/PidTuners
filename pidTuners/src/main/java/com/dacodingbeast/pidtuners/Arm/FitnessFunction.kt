package ArmSpecific

import com.dacodingbeast.pidtuners.Arm.AngleRange
import CommonUtilities.PIDFParams
import com.dacodingbeast.pidtuners.Algorithm.Particle
import kotlin.math.abs
import kotlin.math.pow

/**
 * The Fake Loop Time of the System, commonly known as the Time Stamp
 */
const val Dt = 0.01

/**
 * The Fitness Function made specifically for Arm Mechanisms
 * @param totalTime The Time the system is provided
 * @param angleRange The Target Angle
 * @param badAngleRange The Obstacles in the system
 */

class FitnessFunctionData(val itae: Double,val history:ArrayList<ArmSimData>)

class FitnessFunction(
    private val totalTime: Double,
    private val angleRange: AngleRange,
    private val badAngleRange: AngleRange? = null
) {

    /**
     * The Computation of the [param] to find the fitness score.
     * The lower the fitness score the better: This function minimizes the ITAE
     */

    fun computeParticle(param: Particle): FitnessFunctionData {
        val (p, i, d, f) = param.position.particleParams
        /**
         * @see ArmSim
         */
        val armSim = ArmSim(angleRange, badAngleRange, PIDFParams(p, i, d, f))

        /**
         * The Integral TIme Absolute Error of the Control System
         */

        var itae = 0.0
        val history = ArrayList<ArmSimData>()

        val initialDirection = AngleRange.findMotorDirection(angleRange, badAngleRange)

        //Takes more time but gives better representation
        var time = Dt
        while (time <= totalTime) {
            val update = armSim.updateSim()
            val error = update.error
            history.add( ArmSimData(update.armAngle, update.motorPower, error))

//            println("$error  ${update.first.start}")

            /**
             * System cannot surpass the target by this many radians.
             * If not followed, the system increases the ITAE to prevent it from reoccurring
             */
            val oscilationAngleLimit = Math.toRadians(5.0)
            when (initialDirection) {
                Direction.Clockwise -> if (Math.toDegrees(error) >= oscilationAngleLimit) itae += 200
                Direction.CounterClockWise -> if (Math.toDegrees(error) <= -oscilationAngleLimit) itae += 200
            }

            itae += time.pow(3) * abs(error)
            time += Dt
        }

        /**
         * Final Angle Accuracy - ITAE increases if off by more than one degree once the time is up
         */
//        itae += 1600 * abs(armSim.updateSim().third)

        val error = abs(armSim.updateSim().error)
        if(error>= Math.toRadians(3.0))  itae+= abs(armSim.updateSim().error) *1000
        /**
         * Final Velocity Accuracy - ITAE increases if velocity of arm is not relatively low once the time is up
         */
        if(armSim.angularVelocity>= 1.0) itae +=20 * abs( armSim.angularVelocity)

        // Return ITAE as the fitness score (lower is better)
        return FitnessFunctionData(itae, history)
    }

}