package com.dacodingbeast.pidtuners.Simulators

import CommonUtilities.PIDParams
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.Constants.Constants
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.qualcomm.robotcore.hardware.DcMotorSimple

abstract class SimulatorStructure(open val target: Target, open val obstacle: List<Target>)
{
    lateinit var pidController : PIDFcontroller

    fun init(params: Particle){
        pidController = PIDFcontroller(PIDParams(params.position))
    }

    val constants = PSO_Optimizer.constants

    /**
     * Simulate Robot
     */
    abstract fun updateSimulator(): SimulatorData

    val error = 0.0
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
