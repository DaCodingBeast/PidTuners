package com.dacodingbeast.pidtuners.TypeSpecific.Slides

import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.Simulators.Target
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange.Angles.wrap
import kotlin.math.PI

class SlideRange private constructor(override val start: Double, override val stop: Double): Target(start,stop){
    companion object Slides{
        fun fromTicks(start: Double, end: Double): SlideRange {
            return SlideRange(start, end)
        }
        fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
            return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
        }
    }
}