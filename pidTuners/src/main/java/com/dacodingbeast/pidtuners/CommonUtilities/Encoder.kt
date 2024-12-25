package ArmSpecific

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Encoder(name: String, ahwMap: HardwareMap, encoderDirection: DcMotorSimple.Direction?) {
    var motor: DcMotorEx = ahwMap.get(DcMotorEx::class.java, name)

    init {
        motor.direction = encoderDirection
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    fun getCurrentPosition(): Int {
        return motor.currentPosition
    }
}