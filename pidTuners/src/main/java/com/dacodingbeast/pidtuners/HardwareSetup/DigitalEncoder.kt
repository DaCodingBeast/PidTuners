package com.dacodingbeast.pidtuners.HardwareSetup

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class DigitalEncoder(override val name: String,private val encoderDirection:DcMotorSimple.Direction) : Encoders(name) {
    constructor(name: String) : this(name, DcMotorSimple.Direction.FORWARD)
    private lateinit var motor:DcMotorEx
    override fun init(ahwMap: HardwareMap) {
        motor = ahwMap.get(DcMotorEx::class.java, name)
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motor.direction = encoderDirection
    }
    override fun getCurrentPosition(): Int {
        return motor.currentPosition
    }
}
