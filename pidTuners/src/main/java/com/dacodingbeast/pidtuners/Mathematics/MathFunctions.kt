package com.dacodingbeast.pidtuners.Mathematics

import ArmSpecific.Hardware


class MathFunctions {

    companion object {
    fun applyGearRatio(motorSpecs: Hardware.MotorSpecs, gearRatio:Double) : Hardware.MotorSpecs {
        return Hardware.MotorSpecs(
            rpm = motorSpecs.rpm * gearRatio,
            stallTorque = motorSpecs.stallTorque * (1 / gearRatio),
            customGearRatio = motorSpecs.customGearRatio * gearRatio,
            encoderTicksPerRotation = motorSpecs.encoderTicksPerRotation * (1 / gearRatio)
        )
    }
    fun undoGearRatio(motorSpecs: Hardware.MotorSpecs, gearRatio:Double) : Hardware.MotorSpecs {
        return Hardware.MotorSpecs(
            rpm = motorSpecs.rpm / gearRatio,
            stallTorque = motorSpecs.stallTorque / (1 / gearRatio),
            customGearRatio = motorSpecs.customGearRatio * gearRatio,
            encoderTicksPerRotation = motorSpecs.encoderTicksPerRotation / (1 / gearRatio)
        )
    }
        fun nmToKgcm(nm: Double): Double {
            return nm * 10.1971621
        }
        fun ozInToKgcm(ozIn: Double): Double {
            return ozIn * 0.07200778893234
        }


    }
}