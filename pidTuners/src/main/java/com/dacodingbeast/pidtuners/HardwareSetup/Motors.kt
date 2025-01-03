package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Simulators.Target
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

//todo external encoder optional param
abstract class Motors(
    val name: String,
    val motorDirection: DcMotorSimple.Direction,
    val motorSpecs: MotorSpecs,
    val externalGearRatio: Double = 1.0,
    val pidParams: PIDParams = PIDParams(0.0,0.0,0.0,0.0),
    val externalEncoder: Encoder? = null
) {

    lateinit var hardwareMap: HardwareMap
    var startPosition = 0.0
    lateinit var motor: DcMotorEx

    abstract val obstacle: Target?
    abstract val targets: List<Target>
    val pidController = PIDFcontroller(pidParams)


    fun init(hardwareMap: HardwareMap, startPosition: Double){
        this.hardwareMap = hardwareMap
        this.startPosition = startPosition
        this.motor = hardwareMap.get(DcMotorEx::class.java, name)
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.power = 0.0
        externalEncoder?.init(hardwareMap)
    }

    fun run(targetIndex: Int){
        motor.power=pidController.calculate(targets[targetIndex], obstacle).motorPower
    }

}