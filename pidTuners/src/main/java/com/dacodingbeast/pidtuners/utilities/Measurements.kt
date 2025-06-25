package com.dacodingbeast.pidtuners.utilities

enum class AngleUnit {
    RADIANS,
    DEGREES;

    companion object {
        fun preferredInputType(): AngleUnit {
            return DEGREES
        }
    }
}

enum class DistanceUnit {
    TICKS,
    CM,
    INCHES;

    companion object {
        fun preferredInputType(): DistanceUnit {
            return INCHES
        }
    }
}

class Measurements {
    class Angle @JvmOverloads constructor(
        val number: Double,
        val unit: AngleUnit = AngleUnit.preferredInputType()
    ) {
        companion object {
            @JvmStatic
            fun ofDegrees(degrees: Double): Angle {
                return Angle(degrees, AngleUnit.DEGREES)
            }

            @JvmStatic
            fun ofRadians(radians: Double): Angle {
                return Angle(radians, AngleUnit.RADIANS)
            }

            @JvmStatic
            fun of(number: Double, unit: AngleUnit): Angle {
                return Angle(number, unit)
            }
        }

        fun toDegrees(): Double {
            return when (unit) {
                AngleUnit.RADIANS -> Math.toDegrees(number)
                AngleUnit.DEGREES -> number
            }
        }

        fun toRadians(): Double {
            return when (unit) {
                AngleUnit.RADIANS -> number
                AngleUnit.DEGREES -> Math.toRadians(number)
            }
        }

        fun toAngleUnit(newUnit: AngleUnit): Angle {
            return when (newUnit) {
                AngleUnit.RADIANS -> Angle(toRadians(), newUnit)
                AngleUnit.DEGREES -> Angle(toDegrees(), newUnit)
            }
        }

        fun wrap(): Angle {
            return Angle(this.toRadians() % (2 * Math.PI), AngleUnit.RADIANS)
        }

        fun normalize(): Double {
            val twoPi = 2 * Math.PI
            return if (toRadians() < 0) toRadians() + twoPi else toRadians()
        }
    }

    class Distance @JvmOverloads constructor(
        val number: Double,
        val unit: DistanceUnit = DistanceUnit.preferredInputType()
    ) {
        companion object {
            @JvmStatic
            fun ofInches(inches: Double): Distance {
                return Distance(inches, DistanceUnit.INCHES)
            }

            @JvmStatic
            fun ofTicks(ticks: Double): Distance {
                return Distance(ticks, DistanceUnit.TICKS)
            }

            @JvmStatic
            fun ofCm(cm: Double): Distance {
                return Distance(cm, DistanceUnit.CM)
            }

            @JvmStatic
            fun of(number: Double, unit: DistanceUnit): Distance {
                return Distance(number, unit)
            }
        }

        fun toInches(ticksPerInch: Double): Double { // ticks/inches = ticksPerInch
            return when (unit) {
                DistanceUnit.INCHES -> number
                DistanceUnit.TICKS -> number * 1 / ticksPerInch
                DistanceUnit.CM -> number / 2.54
            }
        }

        fun toTicks(ticksPerInch: Double): Double {
            return when (unit) {
                DistanceUnit.TICKS -> number
                DistanceUnit.INCHES -> number * ticksPerInch
                DistanceUnit.CM -> (number / 2.54) * ticksPerInch
            }
        }

        fun toCm(ticksPerInch: Double): Double {
            return when (unit) {
                DistanceUnit.CM -> number
                DistanceUnit.INCHES -> number * 2.54
                DistanceUnit.TICKS -> number * (1 / ticksPerInch) * 2.54
            }
        }

        fun toDistanceUnit(newUnit: DistanceUnit, ticksPerInch: Double): Distance {
            return when (newUnit) {
                DistanceUnit.INCHES -> Distance(toInches(ticksPerInch), newUnit)
                DistanceUnit.TICKS -> Distance(toTicks(ticksPerInch), newUnit)
                DistanceUnit.CM -> Distance(toCm(ticksPerInch), newUnit)
            }
        }
    }
}