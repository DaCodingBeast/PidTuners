package ArmSpecific

import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import com.dacodingbeast.pidtuners.Mathematics.MathFunctions
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
        val MAX = MotorSpecs(100.0, MathFunctions.nmToKgcm(4.94), 60.0, 1440.0)
    }

    object NeveRest {// in oz in
        val `Classic_60` = MotorSpecs(
            105.0, //free speed
            3.707, MathFunctions.ozInToKgcm(60.0), 1680.0
        )
        val `Classic_40` = MotorSpecs(
            160.0, //free speed
            2.47, MathFunctions.ozInToKgcm(40.0), 1120.0
        )
        val `Orbital_3_7` = MotorSpecs(
            1780.0, //free speed
            MathFunctions.ozInToKgcm(0.228), 3.7, 103.6
        )
        val `Orbital_20` = MotorSpecs(
            340.0, //free speed
            MathFunctions.ozInToKgcm(1.2357), 19.2, 537.6
        )
    }

    object REVCoreHex { //nm
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
        val `40_1` = MotorSpecs(150.0, MathFunctions.nmToKgcm(4.2), 40.0, 28.0 *(1 / 40))

        @JvmField
        val `20_1` = MotorSpecs(300.0, MathFunctions.nmToKgcm(2.1), 20.0, 28.0 * (1/20))
    }

    /**
     * HDHex Motor constructor for all gear ratios
     */

    enum class HDHexGearRatios(val value: Double) {
        `3_1`(3.0),
        `4_1`(4.0),
        `5_1`(5.0)
    }

    class HDHex(vararg grs: HDHexGearRatios) {//nm
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
            motorSpecs = MathFunctions.applyGearRatio(MotorSpecs(
                rpm = baseRpm,
                stallTorque =MathFunctions.nmToKgcm( baseStallTorque),
                customGearRatio = 1.0,
                encoderTicksPerRotation = 28.0
            ),gearRatio)
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
        var customGearRatio: Double,
        val encoderTicksPerRotation: Double
    )

    class Motor(
        val name: String,
        private var motorDirection: DcMotorSimple.Direction,
        private val specs: MotorSpecs,
        externalGearRatio:Double, // should be 1 if no gear ratio
        private var encoderName: String?,
        private var encoderDirection: DcMotorSimple.Direction? = null
    ) {
        lateinit var motor: DcMotorEx
        private var encoder: Encoder? = null
        lateinit var ahwMap: HardwareMap
        private var correctedSpecs = specs

        fun setup(ahwMap: HardwareMap) {
            this.ahwMap = ahwMap
            motor = ahwMap.get(DcMotorEx::class.java, name)
            motor.direction = this.motorDirection
            motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            motor.power = 0.0
            encoder =
                encoderName?.let { Encoder(it, ahwMap, encoderDirection) }
            if (encoder != null) {
                specs.customGearRatio = 1.0
            }
        }

        init {
            if (specs.encoderTicksPerRotation == 0.0) {
                throw IllegalArgumentException("Encoder Ticks per Rotation cannot be 0")
            } else if (specs.encoderTicksPerRotation < 0.0) {
                throw IllegalArgumentException("Encoder Ticks per Rotation cannot be negative")
            }
            if (externalGearRatio != 1.0) {
                correctedSpecs = MathFunctions.applyGearRatio(specs,externalGearRatio)
            }
        }

        fun getSpecs(): MotorSpecs {
            return correctedSpecs
        }

        fun getTicksPerRotation(): Double {
            return correctedSpecs.encoderTicksPerRotation
        }

        fun getCurrentPose(): Double {
            return encoder?.getCurrentPosition()?.toDouble() ?: motor.currentPosition.toDouble()
        }
        fun setPower(power: Double) {
            motor.power = power
        }
        fun getPower(): Double {
            return motor.power
        }

        fun findAngle(angleOffset:Double = 0.0): Double {
            val ticks = getCurrentPose()
            val angle = AngleRange.wrap(ticks * (2 * Math.PI / this.specs.encoderTicksPerRotation))
            return AngleRange.wrap(angle + angleOffset)
        }
    }
}
