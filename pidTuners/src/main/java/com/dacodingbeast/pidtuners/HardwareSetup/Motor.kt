package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Motor(
    val name: String,
    private var motorDirection: DcMotorSimple.Direction,
    private var specs: MotorSpecs,
    externalGearRatio: Double = 1.0,
    private val encoder: Encoder?
) {

    init {
        if (specs.encoderTicksPerRotation == 0.0) {
            throw IllegalArgumentException("Encoder Ticks per Rotation cannot be 0")
        } else if (specs.encoderTicksPerRotation < 0.0) {
            throw IllegalArgumentException("Encoder Ticks per Rotation cannot be negative")
        }

        specs.customGearRatio = externalGearRatio
        specs.applyGearRatio()
    }


    lateinit var motor: DcMotorEx
    lateinit var ahwMap: HardwareMap
    private var stationaryAngle = 0.0

    fun init(ahwMap: HardwareMap, stationaryAngle: Double = 0.0) {
        this.stationaryAngle = stationaryAngle
        this.ahwMap = ahwMap
        motor = ahwMap.get(DcMotorEx::class.java, name)
        motor.direction = this.motorDirection
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.power = 0.0
        encoder?.init(ahwMap)
    }


    fun getSpecs(): MotorSpecs {
        return specs
    }

    fun getTicksPerRotation(): Double {
        return specs.encoderTicksPerRotation
    }

    fun getCurrentPose(): Double {
        return encoder?.getCurrentPosition()?.toDouble() ?: motor.currentPosition.toDouble()
    }

    fun setPower(power: Double) {
        motor.power = power
    }

    fun getPower(): Double {
        return motor.power
    }

    /**
     * To find angle in degrees: Angle.fromRadians(
     */
    fun findAngle(inDegrees : Boolean = false): Double {
        val ticks = getCurrentPose()
        val angle = AngleRange.wrap(stationaryAngle + (ticks * (2 * Math.PI / this.specs.encoderTicksPerRotation)))
        return if (inDegrees) angle * 180 / Math.PI else angle
    }
}