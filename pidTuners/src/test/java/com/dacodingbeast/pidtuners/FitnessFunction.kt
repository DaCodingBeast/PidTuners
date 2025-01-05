//package com.example.pso4pid
//
//import com.dacodingbeast.pidtuners.Algorithm.FitnessFunction
//import com.dacodingbeast.pidtuners.Simulators.AngleRange
//import com.dacodingbeast.pidtuners.Algorithm.Particle
//import com.dacodingbeast.pidtuners.Algorithm.Ranges
//import com.dacodingbeast.pidtuners.Simulators.SimulatorType
//import org.junit.Test
//import kotlin.math.PI
//import kotlin.random.Random
//
//class FitnessFunction {
//
//    @Test
//    fun itaeReturnValue(){
//        val range = Ranges(0.0, Random.nextDouble(0.0,1.0))
//        val ranges = arrayListOf(range,range,range,range)
//        val particle = Particle(ranges, com.dacodingbeast.pidtuners.Algorithm.FitnessFunction(2.0, AngleRange.fromRadians(0.0,PI/2), AngleRange.fromRadians(PI/6,PI/4),SimulatorType.ArmSimulator))
//
//        val fitnessFunction = FitnessFunction(2.0, AngleRange.fromRadians(0.0,PI/2), AngleRange.fromRadians(PI/6,PI/4),SimulatorType.ArmSimulator)
//
//        val result = fitnessFunction.findFitness(particle)
//        println(result.itae)
//    }
//}