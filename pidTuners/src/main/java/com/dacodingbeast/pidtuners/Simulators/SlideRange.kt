package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.dacodingbeast.pidtuners.utilities.Measurements

class SlideRange private constructor(
    override val start: Double,
    override val stop: Double,
    val unit: DistanceUnit
) : Target(start, stop) {

    companion object {

        @JvmStatic
        fun from(unit: DistanceUnit, start: Double, stop: Double, slideMotor: SlideMotor? = null): SlideRange {
            return if (slideMotor == null) {
                SlideRange(start, stop, unit)
            } else {
                val startInInches = Measurements.Distance(start, unit).toInches(slideMotor.conversions.ticksPerInch)
                val stopInInches = Measurements.Distance(stop, unit).toInches(slideMotor.conversions.ticksPerInch)
                SlideRange(startInInches, stopInInches, DistanceUnit.INCHES)
            }
        }

        @JvmStatic fun fromCM(start: Double, stop: Double, slideMotor: SlideMotor? = null) = from(DistanceUnit.CM, start, stop, slideMotor)
        @JvmStatic fun fromInches(start: Double, stop: Double, slideMotor: SlideMotor? = null) = from(DistanceUnit.INCHES, start, stop, slideMotor)
        @JvmStatic fun fromTicks(start: Double, stop: Double, slideMotor: SlideMotor? = null) = from(DistanceUnit.TICKS, start, stop, slideMotor)
    }

    fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
        return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
    }

    fun toInches(slideMotor: SlideMotor): SlideRange {
        return SlideRange(
            Measurements.Distance(start, unit).toInches(slideMotor.conversions.ticksPerInch),
            Measurements.Distance(stop, unit).toInches(slideMotor.conversions.ticksPerInch),
            DistanceUnit.INCHES
        )
    }

    fun toTicks(slideMotor: SlideMotor): SlideRange {
        return SlideRange(
            Measurements.Distance(start, unit).toTicks(slideMotor.conversions.ticksPerInch),
            Measurements.Distance(stop, unit).toTicks(slideMotor.conversions.ticksPerInch),
            DistanceUnit.TICKS
        )
    }

    fun asArrayList(): ArrayList<SlideRange> = arrayListOf(this)
}
