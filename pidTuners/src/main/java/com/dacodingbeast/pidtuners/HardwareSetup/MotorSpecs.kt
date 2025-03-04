package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit

/**
 * Holds the necessary specs needed for this simulation, all which can be found on the vendor's website
 * @param rpm Theoretical rpm
 * @param stallTorque The motors maximum Torque in Kg.cm
 * @param motorGearRatio Any gear conversions that need to be considered
 * Gear ratio is in the form of a fraction: (Motor gear teeth) / (Arm Gear Teeth)
 */
data class MotorSpecs(
    var rpm: Double,
    var stallTorque: StallTorque,
    var motorGearRatio: Double = 1.0,
    var encoderTicksPerRotation: Double,
) {
    constructor(
        rpm: Double,
        stallTorque: StallTorque,
        encoderTicksPerRotation: Double,
    ) : this(rpm, stallTorque, 1.0, encoderTicksPerRotation)

    init {
        stallTorque.to(TorqueUnit.KILOGRAM_CENTIMETER)
        if (motorGearRatio == 0.0) {
            throw IllegalArgumentException("Gear Ratio cannot be 0")
        } else if (motorGearRatio < 0.0) {
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

    fun applyGearRatio(gearRatio: Double) {
        motorGearRatio *= gearRatio
        rpm *= 1 / gearRatio
        stallTorque.value *= gearRatio
        encoderTicksPerRotation *= gearRatio
    }
}