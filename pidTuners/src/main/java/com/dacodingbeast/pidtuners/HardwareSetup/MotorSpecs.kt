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