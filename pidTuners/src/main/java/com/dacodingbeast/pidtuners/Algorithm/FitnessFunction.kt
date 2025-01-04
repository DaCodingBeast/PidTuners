package com.dacodingbeast.pidtuners.Algorithm

import ArmSpecific.ArmSim
import android.transition.Slide
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.Simulators.SimulatorData
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.dacodingbeast.pidtuners.Simulators.Target
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.Simulators.SlideSim
import kotlin.math.abs
import kotlin.math.pow

/**
 * The Fake Loop Time of the System, commonly known as the Time Stamp
 */
const val Dt = 0.01

class FitnessFunctionData(val itae: Double,val history:ArrayList<SimulatorData>)

/**
 * The Fitness Function made specifically for Arm Mechanisms
 * @param totalTime The Time the system is provided
 * @param targets The Targets
 * @param obstacle The Obstacle in the system
 */
class FitnessFunction(
    private val totalTime: Double,
    target: Target,
    private val motor: Motors,
    val targetIndex: Int
) {

    private val simulator = when (motor) {
    is ArmMotor -> ArmSim(motor, targetIndex)
    is SlideMotor -> SlideSim(motor, targetIndex)
    else -> throw IllegalArgumentException("Unsupported motor type")
}

    /**
     * The Computation of the [param] to find the fitness score.
     * The lower the fitness score the better: This function minimizes the ITAE
     */

    fun findFitness(params: Particle): FitnessFunctionData {
        simulator.init(params)

        var itae = 0.0
        val history = ArrayList<SimulatorData>()

        var time = Dt
        while (time <= totalTime) {

            val result = simulator.updateSimulator()

            history.add(result)

            itae += time.pow(3) * abs(simulator.error)
            time += Dt
        }

        itae+= simulator.punishSimulator()

        // Return ITAE as the fitness score (lower is better)
        return FitnessFunctionData(itae, history)
    }

}