package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import java.util.ArrayList

class SlideRange private constructor(override val start: Double, override val stop: Double): Target(start,stop){
    companion object Slides{
        fun fromTicks(start: Double, end: Double): SlideRange {
            return SlideRange(start, end)
        }
        fun fromInches(start: Double, end: Double,motor:SlideMotor): SlideRange {
            return SlideRange(start*motor.ticksPerIn, end*motor.ticksPerIn) //immediately converts to ticks
        }
        fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
            return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
        }
    }
    fun asArrayList(): ArrayList<SlideRange> {
        return arrayListOf(this)
    }
}