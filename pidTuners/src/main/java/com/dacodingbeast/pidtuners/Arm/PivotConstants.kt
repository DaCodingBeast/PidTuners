package com.dacodingbeast.pidtuners.Arm

import ArmSpecific.pso4Arms
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.HardwareSetup.Motor

data class PivotConstants(
    val motor: Motor,
    val testingAngle: AngleRange,
    val obstacle : AngleRange,
    val systemConstants: SystemConstants,
    val sim :pso4Arms,
    val gravityRecord:Boolean,
    val gravityDisplayDataPoints:Boolean,
    val gravityMotorPower:Double,
    var PIDFController: PIDFcontroller
)
