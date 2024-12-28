package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * The Motor class is used to define the motor object, and its properties
 * @param name The name of the motor in the hardware map
 * @param motorDirection The direction of the motor
 * @param specs The specs of the motor, as found on website
 * @param externalGearRatio The external gear ratio of the motor, 1.0 if not geared
 * @param encoder The encoder object
 * @see MotorSpecs
 * @see Encoder
 */
class Motor(
    val name: String,
    private var motorDirection: DcMotorSimple.Direction,
    var specs: MotorSpecs,
    private var externalGearRatio: Double = 1.0,
    private val encoder: Encoder?
) {

    init {
        if (externalGearRatio < 0) {
            throw IllegalArgumentException("Gear ratio must be positive")
        }else if (externalGearRatio == 0.0){
            throw IllegalArgumentException("Gear ratio cannot be zero use 1 if not geared")
        }
        if (encoder != null) { // if using an external encoder, the motor gear ratio is 1 as nothing is geared past that
            externalGearRatio = 1.0
            specs.motorGearRatio = 1.0
        }else { // else, apply the external gear ratio to the motor gear ratio, to find total gear ratio
            specs.applyGearRatio(externalGearRatio)
        }
    }


    lateinit var motor: DcMotorEx
    lateinit var ahwMap: HardwareMap
    private var stationaryAngle = 0.0

    fun init(ahwMap: HardwareMap, stationaryAngle: Double = 0.0) {
        this.stationaryAngle = stationaryAngle
        this.ahwMap = ahwMap
        motor = ahwMap.get(DcMotorEx::class.java, name)
        motor.direction = this.motorDirection
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.power = 0.0
        encoder?.init(ahwMap)
    }

    fun getRPM(): Double {
        return specs.rpm
    }
    fun getGearRatio(): Double {
        return specs.motorGearRatio
    }
    fun getStallTorque(): Double {
        return specs.stallTorque.value
    }

    fun getTicksPerRotation(): Double {
        return specs.encoderTicksPerRotation
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

    /**
     * To find angle in degrees: Angle.fromRadians(
     */
    fun findAngle(inDegrees : Boolean = false): Double {
        val ticks = getCurrentPose()
        val angle = AngleRange.wrap(stationaryAngle + (ticks * (2 * Math.PI / this.specs.encoderTicksPerRotation)))
        return if (inDegrees) angle * 180 / Math.PI else angle
    }
}