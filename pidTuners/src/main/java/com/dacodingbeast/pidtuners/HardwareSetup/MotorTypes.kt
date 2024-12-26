package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * A collection of Motor Brands and characteristics
 */

sealed class Hardware {
    /**
     * Yellow Jacket Motors ranging from 84 - 1150 RPM
     */
    object YellowJacket { //TODO: these all in kg cm
        @JvmField
        val RPM30 = MotorSpecs(
            30.0,250.0,
            ((((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),
            5281.0
        )

        @JvmField
        val RPM43 = MotorSpecs(
            43.0,185.0,
            ((((1 + (46 / 11))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            3895.9
        )

        @JvmField
        val RPM60 = MotorSpecs(
            60.0,133.2,
            ((((1 + (46 / 17))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            2786.2
        )

        @JvmField
        val RPM84 = MotorSpecs(
            84.0,93.6,
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 11)) * 28).toDouble(),
            1993.6
        )

        @JvmField
        val RPM117 = MotorSpecs(
            117.0,68.4,
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17)) * 28).toDouble(),
            1425.1
        )

        @JvmField
        val RPM223 =
            MotorSpecs(223.0, 38.0,((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(),751.8 )

        @JvmField
        val RPM312 =
            MotorSpecs(312.0,24.3, ((((1 + (46 / 17))) * (1 + (46 / 11))) * 28).toDouble(), 537.7)

        @JvmField
        val RPM435 =
            MotorSpecs(435.0,18.7, ((((1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),384.5 )

        @JvmField
        val RPM1150 = MotorSpecs(1150.0,7.9, ((1 + (46 / 11)) * 28).toDouble(),145.1 )

        @JvmField
        val RPM1620 = MotorSpecs(1620.0,5.4, ((1 + (46 / 17)) * 28).toDouble(),103.8 )

        @JvmField
        val RPM6000 = MotorSpecs(6000.0, 1.47,1.0, 28.0)
    }

    object TorqueNado { //torque in nm
        @JvmField
        val MAX = MotorSpecs(100.0, 4.94, 60.0, 1440.0)
    }

    object NeveRest {// in oz in
    @JvmField
        val `Classic_60` = MotorSpecs(
            105.0, //free speed
            3.707, 60.0, 1680.0
        )
        @JvmField
        val `Classic_40` = MotorSpecs(
            160.0, //free speed
            2.47, 40.0, 1120.0
        )
        @JvmField
        val `Orbital_3_7` = MotorSpecs(
            1780.0, //free speed
            0.228, 3.7, 103.6
        )
        @JvmField
        val `Orbital_20` = MotorSpecs(
            340.0, //free speed
            1.2357, 19.2, 537.6
        )
    }

    object REVCoreHex { //nm
        @JvmField
        val CoreHexMotor = MotorSpecs(
            125.0,//free speed
            3.2, 72.0, 288.0
        )
    }

    /**
     * REV Spur Motors with gear ratios of 40:1 and 20:1
     */
    object REVSpurMotor { //nm
        @JvmField
        val `40_1` = MotorSpecs(150.0, 4.2, 40.0, 28.0 *(1 / 40))

        @JvmField
        val `20_1` = MotorSpecs(300.0, 2.1, 20.0, 28.0 * (1/20))
    }

    /**
     * HDHex Motor constructor for all gear ratios
     */

    enum class HDHexGearRatios(val value: Double) {
        `3_1`(3.0),
        `4_1`(4.0),
        `5_1`(5.0)
    }

    class HDHex(vararg grs: HDHexGearRatios) {
        val motorSpecs: MotorSpecs

        init {
            if (grs.isEmpty()) {
                throw IllegalArgumentException("Gear Ratios cannot be empty")
            }

            var gearRatio = 1.0
            for (gr in grs) {
                gearRatio *= gr.value
            }

            val baseRpm = 6000.0
            val baseStallTorque = 0.105
            val baseEncPerRev = 28.0

            motorSpecs = MotorSpecs(
                rpm = baseRpm,
                stallTorque = baseStallTorque,
                customGearRatio = gearRatio,
                encoderTicksPerRotation = baseEncPerRev
            ).applyGearRatio()
        }
    }

}
