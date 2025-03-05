package com.dacodingbeast.pidtuners.utilities.MathFunctions

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer.Companion.motor
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes

object Conversions {
    @JvmStatic
    fun fromInchesToTicks(value: Double): Double {
        if (motor is SlideMotor) {
            (motor as SlideMotor).calculateInPerTick()
            return value * (motor as SlideMotor).ticksPerIn
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    @JvmStatic
    fun fromTicksToInches(value: Double): Double {
        if (motor is SlideMotor) {
            (motor as SlideMotor).calculateInPerTick()
            return value / (motor as SlideMotor).ticksPerIn
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    @JvmStatic
    fun fromAngleToTicks(angle: Double): Double {
        if (motor is SlideMotor) {
            val ticksPerRotation = motor.motorSpecs.encoderTicksPerRotation
            return (angle / (2 * Math.PI)) * ticksPerRotation
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    @JvmStatic
    fun fromTicksToAngle(ticks: Double): Double {
        if (motor is SlideMotor) {
            val ticksPerRotation = motor.motorSpecs.encoderTicksPerRotation
            return (ticks / ticksPerRotation) * 2 * Math.PI
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }
}
