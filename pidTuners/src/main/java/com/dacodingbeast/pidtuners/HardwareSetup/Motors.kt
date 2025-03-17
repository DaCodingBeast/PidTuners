package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.Target
import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit

abstract class Motors(
    val name: String,
    val motorDirection: DcMotorSimple.Direction,
    val motorSpecs: MotorSpecs,
    val systemConstants: ConstantsSuper,
    private var externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    private val externalEncoder: Encoders? = null
) {
    constructor(
        name: String,
        motorDirection: DcMotorSimple.Direction,
        motorSpecs: MotorSpecs,
        systemConstants: ConstantsSuper,
        externalGearRatio: Double,
        pidParams: PIDParams
    ) : this(name, motorDirection, motorSpecs, systemConstants, externalGearRatio, pidParams, null)

    constructor(
        name: String,
        motorSpecs: MotorSpecs,
        systemConstants: ConstantsSuper,
        externalGearRatio: Double,
        pidParams: PIDParams,
        externalEncoder: Encoders
    ) : this(
        name,
        DcMotorSimple.Direction.FORWARD,
        motorSpecs,
        systemConstants,
        externalGearRatio,
        pidParams,
        externalEncoder
    )

    constructor(
        name: String,
        motorSpecs: MotorSpecs,
        systemConstants: ConstantsSuper,
        externalGearRatio: Double,
        pidParams: PIDParams
    ) : this(
        name,
        DcMotorSimple.Direction.FORWARD,
        motorSpecs,
        systemConstants,
        externalGearRatio,
        pidParams
    )

    private lateinit var hardwareMap: HardwareMap
    private var startPosition = 0.0
    lateinit var motor: DcMotorEx

    abstract val obstacle: Target?
    abstract val targets: List<Target>
    private val pidController = PIDFcontroller(pidParams)

    init {
        if (externalGearRatio < 0) {
            throw IllegalArgumentException("Gear ratio must be positive")
        } else if (externalGearRatio == 0.0) {
            throw IllegalArgumentException("Gear ratio cannot be zero use 1 if not geared")
        }
        if (externalEncoder != null) { // if using an external encoder, the motor gear ratio is 1 as nothing is geared past that
            externalGearRatio = 1.0
            motorSpecs.motorGearRatio = 1.0
        } else { // else, apply the external gear ratio to the motor gear ratio, to find total gear ratio
            motorSpecs.applyGearRatio(externalGearRatio)
        }
//        if (targets.isEmpty()) {
//            throw IllegalArgumentException("Targets List empty, you forgot to add your targets")
//        }

    }


    fun init(hardwareMap: HardwareMap, startPosition: Double) {
        this.hardwareMap = hardwareMap
        this.startPosition = startPosition
        this.motor = hardwareMap.get(DcMotorEx::class.java, name)
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.direction = motorDirection
        motor.power = 0.0
        externalEncoder?.init(hardwareMap)
    }

    fun run(targetIndex: Int): Double {
        //todo if targets multiple list switch constants based on new target
        motor.power = pidController.calculate(targets[targetIndex], obstacle).motorPower
        return targets[targetIndex].start
    }

    fun getCurrentPose(): Double {
        return externalEncoder?.getCurrentPosition()?.toDouble() ?: motor.currentPosition.toDouble()
    }

    fun getRPM(): Double {
        return motorSpecs.rpm
    }

    fun getGearRatio(): Double {
        return motorSpecs.motorGearRatio
    }

    fun getStallTorque(): Double {
        return motorSpecs.stallTorque.value
    }

    fun getTicksPerRotation(): Double {
        return motorSpecs.encoderTicksPerRotation
    }

    fun setPower(power: Double) {
        motor.power = power
    }

    fun getPower(): Double {
        return motor.power
    }

    fun getPIDFController(): PIDFcontroller {
        return pidController
    }

    /**
     * Find the motors torque
     * @param power The power applied to the Motor, derived from the PIDF Controller
     */
    fun calculateTmotor(power: Double): Double {
        return calculateTmotor(power, systemConstants.frictionRPM)
    }

    /**
     * Finding the Motor Torque based on the Systems Constants.
     * This function will need to be ran in the Gravity OpMode, so it must take the constants as parameters
     * @see Hardware.Motor Motor being used
     * @param actualRPM Non-theoretical RPM, tested through Friction OpMode
     * @param power Motor Power
     */

    fun calculateTmotor(power: Double, actualRPM: Double): Double {
        try {
            require(power in -1.0..1.0) //obviously works
        }catch (_: IllegalArgumentException){
            DataLogger.instance.logError("Power must be between -1 and 1")

        }
        //friction influenced max power
        val friction = actualRPM / getRPM()

        return getStallTorque() * friction * power
    }

    open fun targetReached(target: Double, accuracy: Double?): Boolean {
        return true
    }

    open fun getCurrent(currentUnit: CurrentUnit): Double {
        return motor.getCurrent(currentUnit)
    }

    abstract fun findPosition(): Double

    fun findPositionUnwrapped(): Double {
        val ticks = getCurrentPose()
        val angle = (ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation))
        return angle
    }

    fun fromInchesToTicks(value: Double): Double {
        if (this is SlideMotor) {
            this.calculateInPerTick()
            return value * (motor as SlideMotor).ticksPerIn
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    fun fromTicksToInches(value: Double): Double {
        if (this is SlideMotor) {
            this.calculateInPerTick()
            return value / this.ticksPerIn
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    fun fromAngleToTicks(angle: Double): Double {
        if (this is SlideMotor) {
            val ticksPerRotation = this.motorSpecs.encoderTicksPerRotation
            return (angle / (2 * Math.PI)) * ticksPerRotation
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    fun fromTicksToAngle(ticks: Double): Double {
        if (this is SlideMotor) {
            val ticksPerRotation = this.motorSpecs.encoderTicksPerRotation
            return (ticks / ticksPerRotation) * 2 * Math.PI
        } else {
            throw IllegalStateException("Motor is not of type SlideMotor.")
        }
    }

    fun fromInchesToAngle(Inches: Double): Double {
        return fromTicksToAngle(fromInchesToTicks(Inches))
    }

    fun fromAngleToInches(Angle: Double): Double {
        return fromTicksToInches(fromAngleToTicks(Angle))
    }

}
