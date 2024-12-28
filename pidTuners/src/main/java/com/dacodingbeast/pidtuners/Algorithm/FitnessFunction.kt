package com.dacodingbeast.pidtuners.Algorithm

import ArmSpecific.ArmSim
import ArmSpecific.ArmSimData
import ArmSpecific.Direction
import CommonUtilities.PIDFParams
import com.dacodingbeast.pidtuners.Arm.AngleRange
import kotlin.math.abs
import kotlin.math.pow

/**
 * The Fake Loop Time of the System, commonly known as the Time Stamp
 */
const val Dt = 0.01

class FitnessFunctionData(val itae: Double,val history:ArrayList<ArmSimData>)

/**
 * The Fitness Function made specifically for Arm Mechanisms
 * @param totalTime The Time the system is provided
 * @param targets The Targets
 * @param obstacle The Obstacle in the system
 */
class FitnessFunction(
    private val totalTime: Double,
    private val targets: List<Target>,
    private val obstacle: List<Target>,
    private val simulatorType: SimulatorType
) {

    val simulator = Simulator(totalTime,targets,obstacle)

    /**
     * The Computation of the [param] to find the fitness score.
     * The lower the fitness score the better: This function minimizes the ITAE
     */

    fun computeParticle(params: Particle): FitnessFunctionData {
        simulator.init(params)

        var itae = 0.0
        val history = ArrayList<ArmSimData>()

        var time = Dt
        while (time <= totalTime) {

            simulator.update()

            history.add(ArmSimData(simulator.mechanismPosition, simulator.motorPower, simulator.error))

            itae += time.pow(3) * abs(simulator.error)
            time += Dt
        }

        val error = simulator.error

        if(error >= simulator.acceptableError)  itae += simulator.badAccuracy
        if(simulator.mechanismVelcoity >= simulator.acceptableError) itae += simulator.badVelocity

        // Return ITAE as the fitness score (lower is better)
        return FitnessFunctionData(itae, history)
    }

}