package com.dacodingbeast.pidtuners.Simulators

import java.util.ArrayList

class SlideRange private constructor(override val start: Double, override val stop: Double): Target(start,stop){
    companion object Slides{
        fun fromTicks(start: Double, end: Double): SlideRange {
            return SlideRange(start, end)
        }
        fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
            return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
        }
    }
    fun toAngleRange(): AngleRange {
        return AngleRange.fromRadians(start, stop)
    }
    fun asArrayList(): ArrayList<SlideRange> {
        return arrayListOf(this)
    }
}