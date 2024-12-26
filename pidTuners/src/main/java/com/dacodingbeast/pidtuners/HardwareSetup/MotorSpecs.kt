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
    var stallTorque: Double,
    var customGearRatio: Double = 1.0,
    val encoderTicksPerRotation: Double
) {
    fun applyGearRatio(): MotorSpecs {
        return MotorSpecs(
            rpm = this.rpm * this.customGearRatio,
            stallTorque = this.stallTorque * (1 / this.customGearRatio),
            customGearRatio = this.customGearRatio,
            encoderTicksPerRotation = this.encoderTicksPerRotation * (1 / this.customGearRatio)
        )
    }

    fun undoGearRatio(): MotorSpecs {
        return MotorSpecs(
            rpm = this.rpm / this.customGearRatio,
            stallTorque = this.stallTorque / (1 / this.customGearRatio),
            customGearRatio = this.customGearRatio,
            encoderTicksPerRotation = this.encoderTicksPerRotation / (1 / this.customGearRatio)
        )
    }

}