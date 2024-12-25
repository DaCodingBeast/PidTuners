package com.dacodingbeast.pidtuners.Mathematics

import com.dacodingbeast.pidtuners.CommonUtilities.Hardware.MotorSpecs

class MathFunctions {

    companion object {
    fun applyGearRatio(motorSpecs: MotorSpecs,gearRatio:Double) : MotorSpecs {
        return MotorSpecs(
            rpm = motorSpecs.rpm * gearRatio,
            stallTorque = motorSpecs.stallTorque * (1 / gearRatio),
            customGearRatio =motorSpecs.customGearRatio*gearRatio,
            encoderTicksPerRotation = motorSpecs.encoderTicksPerRotation*(1 / gearRatio)
        )
    }
    fun undoGearRatio(motorSpecs: MotorSpecs,gearRatio:Double) : MotorSpecs {
        return MotorSpecs(
            rpm = motorSpecs.rpm / gearRatio,
            stallTorque = motorSpecs.stallTorque / (1 / gearRatio),
            customGearRatio =motorSpecs.customGearRatio*gearRatio,
            encoderTicksPerRotation = motorSpecs.encoderTicksPerRotation/(1 / gearRatio)
        )
    }

    }
}