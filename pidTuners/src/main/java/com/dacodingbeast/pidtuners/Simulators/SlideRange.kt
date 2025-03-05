package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.utilities.MathFunctions.Conversions.fromAngleToTicks
import com.dacodingbeast.pidtuners.utilities.MathFunctions.Conversions.fromInchesToTicks
import com.dacodingbeast.pidtuners.utilities.MathFunctions.Conversions.fromTicksToAngle
import com.dacodingbeast.pidtuners.utilities.MathFunctions.Conversions.fromTicksToInches

class SlideRange private constructor(override val start: Double, override val stop: Double) :
    Target(start, stop) {
    companion object Slides {
        @JvmStatic
        fun fromTicks(start: Double, end: Double): SlideRange {
            return SlideRange(start, end)
        }

        @JvmStatic
        fun fromInches(start: Double, end: Double): SlideRange {
            return SlideRange(fromInchesToTicks(start), fromInchesToTicks(end))
        }

        @JvmStatic
        fun fromAngle(angle1: Double, angle2: Double): SlideRange {
            return SlideRange(fromAngleToTicks(angle1), fromAngleToTicks(angle2))
        }

    }

    fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
        return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
    }

    fun toInches(): SlideRange {
        return SlideRange(fromTicksToInches(this.start), fromTicksToInches(this.stop))
    }

    fun toTicks(): SlideRange {
        return this // Already in ticks, no conversion needed.
    }

    fun toAngle(): SlideRange {
        return SlideRange(fromTicksToAngle(this.start), fromTicksToAngle(this.stop))
    }

    fun asArrayList(): ArrayList<SlideRange> {
        return arrayListOf(this)
    }
}
