package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.TypeSpecific.Arm.SystemConstants
import ArmSpecific.pso4Arms
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange

data class PivotConstants(
    val motor:Motor,
    val testingAngle: AngleRange,
    val obstacle : AngleRange,
    val systemConstants: SystemConstants,
    val sim :pso4Arms,
    var gravityRecord:Boolean,
    val gravityDisplayDataPoints:Boolean,
    val gravityMotorPower:Double,
    var PIDFController: PIDFcontroller,
)