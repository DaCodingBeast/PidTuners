package org.firstinspires.ftc.teamcode.customHardware.pso

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
    object YellowJacket {
        @JvmField
        val RPM30 = MotorSpecs(
            30.0,
            ((((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),
            250.0
        )

        @JvmField
        val RPM43 = MotorSpecs(
            43.0,
            ((((1 + (46 / 11))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            185.0
        )

        @JvmField
        val RPM60 = MotorSpecs(
            60.0,
            ((((1 + (46 / 17))) * (1 + (46 / 11))) * (1 + (46 / 11)) * 28).toDouble(),
            133.2
        )

        @JvmField
        val RPM84 = MotorSpecs(
            84.0,
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 11)) * 28).toDouble(),
            93.6
        )

        @JvmField
        val RPM117 = MotorSpecs(
            117.0,
            ((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17)) * 28).toDouble(),
            68.4
        )

        @JvmField
        val RPM223 =
            MotorSpecs(223.0, ((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(), 38.0)

        @JvmField
        val RPM312 =
            MotorSpecs(312.0, ((((1 + (46 / 17))) * (1 + (46 / 11))) * 28).toDouble(), 24.3)

        @JvmField
        val RPM435 =
            MotorSpecs(435.0, ((((1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(), 18.7)

        @JvmField
        val RPM1150 = MotorSpecs(1150.0, ((1 + (46 / 11)) * 28).toDouble(), 7.9)

        @JvmField
        val RPM1620 = MotorSpecs(1620.0, ((1 + (46 / 17)) * 28).toDouble(), 5.4)

        @JvmField
        val RPM6000 = MotorSpecs(6000.0, 28.0, 1.47)
    }

    object TorqueNado {
        val MAX = MotorSpecs(100.0, 4.94, 60.0, 1440.0)
    }

    object NeveRest {
        val `Classic_60` = MotorSpecs(
            105.0, //free speed
            3.707, 60.0, 1680.0
        )
        val `Classic_40` = MotorSpecs(
            160.0, //free speed
            2.47, 40.0, 1120.0
        )
        val `Orbital_3_7` = MotorSpecs(
            1780.0, //free speed
            0.228, 3.7, 103.6
        )
        val `Orbital_20` = MotorSpecs(
            340.0, //free speed
            1.2357, 19.2, 537.6
        )
    }

    object REVCoreHex {
        val CoreHexMotor = MotorSpecs(
            125.0,//free speed
            3.2, 72.0, 288.0
        )
    }

    /**
     * REV Spur Motors with gear ratios of 40:1 and 20:1
     */
    object REVSpurMotor {
        @JvmField
        val `40_1` = MotorSpecs(150.0, 4.2, 40.0, 28 * 40.0)

        @JvmField
        val `20_1` = MotorSpecs(300.0, 2.1, 20.0, 28 * 20.0)
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
            motorSpecs = MotorSpecs(
                rpm = baseRpm / gearRatio,
                stallTorque = baseStallTorque * gearRatio,
                customGearRatio = gearRatio,
                encoderTicksPerRotation = 28 * gearRatio
            )
        }
    }

    /**
     * Holds the necessary specs needed for this simulation, all which can be found on the vendor's website
     * @param rpm Theoretical rpm
     * @param stallTorque The motors maximum Torque in Kg.cm
     * @param customGearRatio Any gear conversions that need to be considered
     * Gear ratio is in the form of a fraction: (Motor gear teeth) / (Arm Gear Teeth)
     */
    data class MotorSpecs(
        var rpm: Double,
        var stallTorque: Double,
        var customGearRatio: Double = 1.0,
        private val encoderTicksPerRotation: Double? = null
    ) {
        fun getEncoderTicksPerRotation(): Double? {
            return encoderTicksPerRotation
        }
    }

    class Motor(
        val name: String,
        private var motorDirection: DcMotorSimple.Direction,
        private val specs: MotorSpecs,
        private var encoderTicksPerRotation: Double = specs.getEncoderTicksPerRotation() ?: 28.0,
        private var encoderName: String = name,
        private var encoderDirection: DcMotorSimple.Direction? = DcMotorSimple.Direction.FORWARD
    ) {
        lateinit var motor: DcMotorEx
        private var encoder: Encoder? = null
        lateinit var ahwMap: HardwareMap

        fun setup(ahwMap: HardwareMap) {
            this.ahwMap = ahwMap
            motor = ahwMap.get(DcMotorEx::class.java, name)
            motor.direction = this.motorDirection
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            motor.power = 0.0
            encoder =
                if (encoderName != name) Encoder(encoderName, ahwMap, encoderDirection) else null
            if (encoder != null) {
                specs.customGearRatio = 1.0
            }
        }

        init {
            if (encoderTicksPerRotation == 0.0) {
                throw IllegalArgumentException("Encoder Ticks per Rotation cannot be 0")
            } else if (encoderTicksPerRotation < 0.0) {
                throw IllegalArgumentException("Encoder Ticks per Rotation cannot be negative")
            }
            if (specs.customGearRatio != 0.0) {
                specs.rpm *= specs.customGearRatio
                encoderTicksPerRotation *= (1 / specs.customGearRatio)
                specs.stallTorque *= (1 / specs.customGearRatio)
            } else {
                throw IllegalArgumentException("Gear Ratio cannot be 0")
            }
        }

        fun getSpecs(): MotorSpecs {
            return specs
        }

        fun getTicksPerRotation(): Double {
            return encoderTicksPerRotation
        }

        fun getCurrentPose(): Double {
            return encoder?.getCurrentPosition()?.toDouble() ?: motor.currentPosition.toDouble()
        }
    }
}
