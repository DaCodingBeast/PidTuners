package com.dacodingbeast.pidtuners.Algorithm

import ArmSpecific.ArmSim
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.Simulators.SimulatorData
import com.dacodingbeast.pidtuners.Simulators.SlideSim
import kotlin.math.abs

/**
 * The Fake Loop Time of the System, commonly known as the Time Stamp
 */
const val Dt = 0.01

class FitnessFunctionData(val itae: Double, val history: ArrayList<SimulatorData>)

class FitnessFunction(
    private val totalTime: Double,
    motor: Motors,
    targetIndex: Int
) {

    private val simulator = when (motor) {
        is ArmMotor -> ArmSim(motor, targetIndex)
        is SlideMotor -> SlideSim(motor, targetIndex)
        else -> throw IllegalArgumentException("Unsupported motor type")
    }

    /**
     * The Computation of the [params] to find the fitness score.
     * The lower the fitness score the better: This function minimizes the ITAE
     */

    fun findFitness(params: Particle): FitnessFunctionData {
        simulator.init(params)

        var itae = 0.0
        val history = ArrayList<SimulatorData>()

        var time = Dt
        val timeStepCubed = (Dt * Dt * Dt)
        var timeCubed = timeStepCubed

        var stepCount =0

        while (time <= totalTime) {

            val result = simulator.updateSimulator()

            if (stepCount % 10 == 0) {
                history.add(result)
            }

            itae += timeCubed * abs(simulator.error)
            time += Dt
            timeCubed += timeStepCubed
            stepCount++
        }

        itae += simulator.punishSimulator()

        // Return ITAE as the fitness score (lower is better)
        return FitnessFunctionData(itae, history)
    }

}