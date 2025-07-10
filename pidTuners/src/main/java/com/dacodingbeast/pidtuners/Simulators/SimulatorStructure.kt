package com.dacodingbeast.pidtuners.Simulators

import CommonUtilities.SimulatorPIDController
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.HardwareSetup.Motors

abstract class SimulatorStructure(open val motor: Motors, open val targetIndex: Int) {
    lateinit var pidController: SimulatorPIDController

    fun init(params: Particle) {
        pidController = SimulatorPIDController(PIDParams(params.position))
    }

    /**
     * Simulate Robot
     */
    abstract fun updateSimulator(): SimulatorData

    var error = 0.0
    var velocity = 0.0

    /**
     * Punish Fitness based on performance
     */
    abstract fun punishSimulator(): Double

    /**
     * Define the error threshold you would like to stay within
     */
    abstract val acceptableError: Double

    /**
     * Define the fitness punishment if @see[acceptableError]'s threshold isn't reached
     */
    abstract fun badAccuracy(): Double

    /**
     * Define the velocity threshold you would like to stay within
     */
    abstract val acceptableVelocity: Double

    /**
     * Define the fitness punishment if @see[acceptableVelocity]'s criteria isn't met
     */
    abstract fun badVelocity(): Double


}
