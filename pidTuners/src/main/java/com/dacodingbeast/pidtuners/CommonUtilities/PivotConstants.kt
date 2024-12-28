package com.dacodingbeast.pidtuners.CommonUtilities

import com.dacodingbeast.pidtuners.TypeSpecific.Arm.PivotSystemConstants
import ArmSpecific.pso4Arms
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange

data class PivotConstants(
    val motor: Motor,
    val testingAngle: AngleRange,
    val obstacle : AngleRange,
    val systemConstants: PivotSystemConstants,
    val sim :pso4Arms,
    var gravityRecord:Boolean,
    val gravityDisplayDataPoints:Boolean,
    val gravityMotorPower:Double,
    var PIDFController: PIDFcontroller,
)