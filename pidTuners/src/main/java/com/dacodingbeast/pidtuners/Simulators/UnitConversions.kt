package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes

object Conversions {
    @JvmStatic
    fun fromInchesToTicks(value: Double): Double {
        val motor = TuningOpModes.slideMotor ?: throw IllegalStateException("Slide motor is not initialized.")
        motor.calculateInPerTick()
        return value * motor.ticksPerIn
    }

    @JvmStatic
    fun fromTicksToInches(value: Double): Double {
        val motor = TuningOpModes.slideMotor ?: throw IllegalStateException("Slide motor is not initialized.")
        motor.calculateInPerTick()
        return value / motor.ticksPerIn
    }

    @JvmStatic
    fun fromAngleToTicks(angle: Double): Double {
        val motor = TuningOpModes.slideMotor ?: throw IllegalStateException("Slide motor is not initialized.")
        val ticksPerRotation = motor.motorSpecs.encoderTicksPerRotation
        return (angle / (2 * Math.PI)) * ticksPerRotation
    }

    @JvmStatic
    fun fromTicksToAngle(ticks: Double): Double {
        val motor = TuningOpModes.slideMotor ?: throw IllegalStateException("Slide motor is not initialized.")
        val ticksPerRotation = motor.motorSpecs.encoderTicksPerRotation
        return (ticks / ticksPerRotation) * 2 * Math.PI
    }
}
