package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Test

class MotorSpecs {

    @Test
    fun `create_Motor_Spec_bjects`(){

        Hardware.REVSpurMotor.`20_1`
        Hardware.REVSpurMotor.`40_1`
        Hardware.REVCoreHex.CoreHexMotor
        Hardware.TorqueNado.MAX
        Hardware.NeveRest.Classic_40
        Hardware.NeveRest.Classic_60
        Hardware.NeveRest.Orbital_3_7
        Hardware.NeveRest.Orbital_20
        Hardware.HDHex(Hardware.HDHexGearRatios.`3_1`,Hardware.HDHexGearRatios.`4_1`,Hardware.HDHexGearRatios.`5_1`).motorSpecs

    }

}