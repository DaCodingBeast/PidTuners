package com.example.pso4pid

import ArmSpecific.FitnessFunction
import ArmSpecific.pso4Arms
import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import PSO_Algorithm.Particle
import PSO_Algorithm.Ranges
import org.firstinspires.ftc.teamcode.PSO.Arm.Constants
import org.junit.Test
import kotlin.math.PI
import kotlin.random.Random

class FitnessFunction {

    @Test
    fun itaeReturnValue(){
        val range = Ranges(0.0, Random.nextDouble(0.0,1.0))
        val ranges = arrayListOf(range,range,range,range)
        val particle = Particle(ranges, false)

        val fitnessFunction = FitnessFunction(2.0, AngleRange(0.0,PI/2))
        pso4Arms.System.SystemConstants = Constants.constant
        val result = fitnessFunction.computeParticle(particle)
        println(result.itae)
    }
}