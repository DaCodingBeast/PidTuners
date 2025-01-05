package com.dacodingbeast.pidtuners.Simulators

import CommonUtilities.PIDParams
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.HardwareSetup.Motors

abstract class SimulatorStructure(open val motor: Motors, open val targetIndex: Int)
{
    lateinit var pidController : PIDFcontroller

    fun init(params: Particle){
        pidController = PIDFcontroller(PIDParams(params.position))
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
    abstract val badAccuracy : Double

    /**
     * Define the velocity threshold you would like to stay within
     */
    abstract val acceptableVelocity: Double

    /**
     * Define the fitness punishment if @see[acceptableVelocity]'s criteria isn't met
     */
    abstract val badVelocity : Double


}
