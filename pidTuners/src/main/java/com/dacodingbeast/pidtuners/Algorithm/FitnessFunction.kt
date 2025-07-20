package com.dacodingbeast.pidtuners.Algorithm

import com.dacodingbeast.pidtuners.Simulators.ArmSim
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

        var stepCount = 0
        var totalError = 0.0
        var maxError = 0.0

        while (time <= totalTime) {

            val result = simulator.updateSimulator()

            if (stepCount % 10 == 0) {
                history.add(result)
            }

            val errorContribution = timeCubed * abs(simulator.error)
            itae += errorContribution
            totalError += abs(simulator.error)
            maxError = maxOf(maxError, abs(simulator.error))
            
            // Debug output every 100 steps
            if (stepCount % 100 == 0) {
//                println("Step $stepCount: Error=${simulator.error}, TimeCubed=$timeCubed, Contribution=$errorContribution, ITAE=$itae")
            }
            
            time += Dt
            timeCubed += timeStepCubed
            stepCount++
        }

        val punishment = simulator.punishSimulator()
        itae += punishment
        
//        println("=== Fitness Calculation Summary ===")
//        println("Total Steps: $stepCount")
//        println("Total Error Accumulated: $totalError")
//        println("Max Error: $maxError")
//        println("Final ITAE: $itae")
//        println("Punishment: $punishment")
//        println("Final Fitness Score: $itae")
//        println("================================")

        // Return ITAE as the fitness score (lower is better)
        return FitnessFunctionData(itae, history)
    }

}