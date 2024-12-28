package com.example.pso4pid

import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange
import com.dacodingbeast.pidtuners.Simulators.Direction
import org.junit.Test
import kotlin.math.PI

class MotorDirections {


    /**
     * Test Cases are the only reasonable way to make sure you can find the correct motor direction to avoid obstacles
     * Expected Outcome: Clockwise direction, avoiding obstacle
     */
    @Test
    fun checkClockwiseDirectionWithObstacles(){
        val CorrectDirection = Direction.Clockwise
        val obstacle = AngleRange(0.5,1.0)

        val d1 = List(5){ AngleRange.findMotorDirection(AngleRange(0.1,0.8), obstacle)}
        val d2 = List(5){ AngleRange.findMotorDirection(AngleRange(-.1,-PI), obstacle)}
        val d3 = List(5){ AngleRange.findMotorDirection(AngleRange(-.5,-PI/2), obstacle)}
        val d4 = List(5){ AngleRange.findMotorDirection(AngleRange(-.1,.5), obstacle)}
        val DirectionLists = listOf(d1,d2,d3,d4)

        DirectionLists
            .asSequence()
            .flatMap { it.asSequence() }
            .forEach { assert(CorrectDirection == it) }
    }
    /**
     * Test Cases are the only reasonable way to make sure you can find the correct motor direction to avoid obstacles
     * Expected Outcome: CounterClockWise Direction, avoiding obstacle
     */
    @Test
    fun checkCounterClockwiseDirectionWithObstacles(){
        val CorrectDirection = Direction.CounterClockWise
        val obstacle = AngleRange(-0.5,-1.0)

        val d1 = List(5){ AngleRange.findMotorDirection(AngleRange(0.0, PI), obstacle)}
        val d2 = List(5){ AngleRange.findMotorDirection(AngleRange(1.0,2.0), obstacle)}
        val d3 = List(5){ AngleRange.findMotorDirection(AngleRange(-2.0,-1.8), obstacle)}
        val d4 = List(5){ AngleRange.findMotorDirection(AngleRange(-.3,-.8), obstacle)}
        val DirectionLists = listOf(d1,d2,d3,d4)

        DirectionLists
            .asSequence()
            .flatMap { it.asSequence() }
            .forEach { assert(CorrectDirection == it) }
    }


}
