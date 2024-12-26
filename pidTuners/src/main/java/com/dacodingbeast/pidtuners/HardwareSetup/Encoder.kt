package com.dacodingbeast.pidtuners.HardwareSetup

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Encoder(val name: String, private val encoderDirection: DcMotorSimple.Direction?) {
    lateinit var motor: DcMotorEx

    fun init(awHmap : HardwareMap){
        motor = awHmap.get(DcMotorEx::class.java, name)
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motor.direction = encoderDirection
    }

    fun getCurrentPosition(): Int {
        return motor.currentPosition
    }
}