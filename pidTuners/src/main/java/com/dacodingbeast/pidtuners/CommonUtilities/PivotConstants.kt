package com.dacodingbeast.pidtuners.CommonUtilities

import ArmSpecific.ArmAngle
import com.dacodingbeast.pidtuners.ArmSpecific.SystemConstants
import ArmSpecific.pso4Arms
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.Mathematics.AngleRange

data class PivotConstants(
    val motor:Hardware.Motor,
    val testingAngle: AngleRange,
    val obstacle : AngleRange,
    val systemConstants: SystemConstants,
    val sim :pso4Arms,
    val gravityRecord:Boolean,
    val gravityDisplayDataPoints:Boolean,
    val gravityMotorPower:Double,
    var PIDFController: PIDFcontroller,
    val armAngle: ArmAngle
)
