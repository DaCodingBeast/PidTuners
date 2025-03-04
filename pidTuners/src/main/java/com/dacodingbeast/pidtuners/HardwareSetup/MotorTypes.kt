package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit

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
            30.0, StallTorque(250.0, TorqueUnit.KILOGRAM_CENTIMETER),
            ((((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),
            5281.0
        )

        @JvmField
        val RPM43 = MotorSpecs(
            43.0, StallTorque(185.0, TorqueUnit.KILOGRAM_CENTIMETER),
            ((((1 + (46 / 11))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            3895.9
        )

        @JvmField
        val RPM60 = MotorSpecs(
            60.0, StallTorque(133.2, TorqueUnit.KILOGRAM_CENTIMETER),
            ((((1 + (46 / 17))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            2786.2
        )

        @JvmField
        val RPM84 = MotorSpecs(
            84.0, StallTorque(93.6, TorqueUnit.KILOGRAM_CENTIMETER),
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 11)) * 28).toDouble(),
            1993.6
        )

        @JvmField
        val RPM117 = MotorSpecs(
            117.0, StallTorque(68.4, TorqueUnit.KILOGRAM_CENTIMETER),
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17)) * 28).toDouble(),
            1425.1
        )

        @JvmField
        val RPM223 =
            MotorSpecs(
                223.0,
                StallTorque(38.0, TorqueUnit.KILOGRAM_CENTIMETER),
                ((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(),
                751.8
            )

        @JvmField
        val RPM312 =
            MotorSpecs(
                312.0,
                StallTorque(24.3, TorqueUnit.KILOGRAM_CENTIMETER),
                ((((1 + (46 / 17))) * (1 + (46 / 11))) * 28).toDouble(),
                537.7
            )

        @JvmField
        val RPM435 =
            MotorSpecs(
                435.0,
                StallTorque(18.7, TorqueUnit.KILOGRAM_CENTIMETER),
                ((((1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),
                384.5
            )

        @JvmField
        val RPM1150 = MotorSpecs(
            1150.0,
            StallTorque(7.9, TorqueUnit.KILOGRAM_CENTIMETER),
            ((1 + (46 / 11)) * 28).toDouble(),
            145.1
        )

        @JvmField
        val RPM1620 = MotorSpecs(
            1620.0,
            StallTorque(5.4, TorqueUnit.KILOGRAM_CENTIMETER),
            ((1 + (46 / 17)) * 28).toDouble(),
            103.8
        )

        @JvmField
        val RPM6000 =
            MotorSpecs(6000.0, StallTorque(1.47, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0)
    }

    object TorqueNado { //torque in nm
        @JvmField
        val MAX = MotorSpecs(100.0, StallTorque(4.94, TorqueUnit.NEWTON_METER), 60.0, 1440.0)
    }

    object NeveRest {
        // in oz in
        @JvmField
        val `Classic_60` = MotorSpecs(
            105.0, //free speed
            StallTorque(3.707, TorqueUnit.OUNCE_INCH), 60.0, 1680.0
        )

        @JvmField
        val `Classic_40` = MotorSpecs(
            160.0, //free speed
            StallTorque(2.47, TorqueUnit.OUNCE_INCH), 40.0, 1120.0
        )

        @JvmField
        val `Orbital_3_7` = MotorSpecs(
            1780.0, //free speed
            StallTorque(0.228, TorqueUnit.OUNCE_INCH), 3.7, 103.6
        )

        @JvmField
        val `Orbital_20` = MotorSpecs(
            340.0, //free speed
            StallTorque(1.2357, TorqueUnit.OUNCE_INCH), 19.2, 537.6
        )
    }

    object REVCoreHex { //nm
        @JvmField
        val CoreHexMotor = MotorSpecs(
            125.0,//free speed
            StallTorque(3.2, TorqueUnit.KILOGRAM_CENTIMETER), 72.0, 288.0
        )
    }

    /**
     * REV Spur Motors with gear ratios of 40:1 and 20:1
     */
    object REVSpurMotor { //nm
        @JvmField
        val `GR40` =
            MotorSpecs(150.0, StallTorque(4.2, TorqueUnit.NEWTON_METER), 40.0, 28.0 * (1.0 / 40.0))

        @JvmField
        val `GR20` =
            MotorSpecs(300.0, StallTorque(2.1, TorqueUnit.NEWTON_METER), 20.0, 28.0 * (1.0 / 20.0))
    }

    /**
     * HDHex Motor constructor for all gear ratios
     */

    enum class HDHexGearRatios(val value: Double) {
        `GR3_1`(3.0),
        `GR4_1`(4.0),
        `GR5_1`(5.0)
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
                stallTorque = StallTorque(baseStallTorque, TorqueUnit.NEWTON_METER),
                motorGearRatio = 1.0,
                encoderTicksPerRotation = baseEncPerRev
            )

            motorSpecs.applyGearRatio(gearRatio)
        }
    }
}
