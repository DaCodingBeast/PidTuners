package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.dacodingbeast.pidtuners.utilities.Measurements

class SlideRange private constructor(override val start: Double, override val stop: Double,val unit : DistanceUnit) :
    Target(start, stop) {

        companion object Distances{
            @JvmStatic
            fun fromCM(start: Double, stop: Double,slideMotor: SlideMotor?): SlideRange {
                return from(DistanceUnit.CM,start,stop,slideMotor)
            }
            @JvmStatic
            fun fromCM(start: Double, stop: Double): SlideRange {
                return from(DistanceUnit.CM,start,stop,null)
            }
            @JvmStatic
            fun fromInches(start: Double, stop: Double,slideMotor: SlideMotor? = null): SlideRange {
                return from(DistanceUnit.INCHES,start,stop,slideMotor)
            }
            @JvmStatic
            fun fromInches(start: Double, stop: Double): SlideRange {
                return from(DistanceUnit.INCHES,start,stop,null)
            }
            @JvmStatic
            fun fromTicks(start: Double, stop: Double,slideMotor: SlideMotor?): SlideRange {
                return from(DistanceUnit.TICKS,start,stop,slideMotor)
            }
            @JvmStatic
            fun fromTicks(start: Double, stop: Double): SlideRange {
                return from(DistanceUnit.TICKS,start,stop,null)
            }
            @JvmStatic
            private fun from(unit: DistanceUnit, start: Double, stop: Double,slideMotor: SlideMotor?): SlideRange {
                if (slideMotor == null) return SlideRange(start,stop,unit)

                return SlideRange(Measurements.Distance(start,unit).toTicks(slideMotor.conversions.ticksPerInch), Measurements.Distance(stop,unit).toTicks(slideMotor.conversions.ticksPerInch),
                    DistanceUnit.TICKS) // should always store in ticks
            }
        }

    fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
        return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
    }
    fun toInches(slideMotor: SlideMotor): SlideRange {
        return SlideRange(Measurements.Distance(start,unit).toInches(slideMotor.conversions.ticksPerInch), Measurements.Distance(stop,unit).toInches(slideMotor.conversions.ticksPerInch), DistanceUnit.INCHES)
    }

    fun asArrayList(): ArrayList<SlideRange> {
        return arrayListOf(this)
    }
}
