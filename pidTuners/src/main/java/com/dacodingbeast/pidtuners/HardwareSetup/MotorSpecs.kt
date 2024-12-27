package com.dacodingbeast.pidtuners.HardwareSetup

/**
 * Holds the necessary specs needed for this simulation, all which can be found on the vendor's website
 * @param rpm Theoretical rpm
 * @param stallTorque The motors maximum Torque in Kg.cm
 * @param customGearRatio Any gear conversions that need to be considered
 * Gear ratio is in the form of a fraction: (Motor gear teeth) / (Arm Gear Teeth)
 */
data class MotorSpecs(
    var rpm: Double,
    var stallTorque: StallTorque,
    var customGearRatio: Double = 1.0,
    var encoderTicksPerRotation: Double,
) {
    init {
        stallTorque.to(TorqueUnit.KILOGRAM_CENTIMETER)
        if (customGearRatio == 0.0) {
            throw IllegalArgumentException("Gear Ratio cannot be 0")
        } else if (customGearRatio < 0.0) {
            throw IllegalArgumentException("Gear Ratio cannot be negative")
        }
        if (encoderTicksPerRotation == 0.0) {
            throw IllegalArgumentException("Encoder Ticks per Rotation cannot be 0")
        } else if (encoderTicksPerRotation < 0.0) {
            throw IllegalArgumentException("Encoder Ticks per Rotation cannot be negative")
        }
        if (rpm == 0.0) {
            throw IllegalArgumentException("RPM cannot be 0")
        } else if (rpm < 0.0) {
            throw IllegalArgumentException("RPM cannot be negative")
        }
    }

    fun applyGearRatio() {
        rpm *= customGearRatio
        stallTorque.value *= (1 / customGearRatio)
        encoderTicksPerRotation *= (1 / customGearRatio)
    }

    fun undoGearRatio() {
        rpm /= customGearRatio
        stallTorque.value /= (1 / customGearRatio)
        encoderTicksPerRotation /= (1 / customGearRatio)
    }
}