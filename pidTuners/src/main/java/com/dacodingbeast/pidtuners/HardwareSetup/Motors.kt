package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.SimulatorPIDController
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
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
    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    private val externalEncoder: Encoders? = null
) {


    private lateinit var hardwareMap: HardwareMap
    private var startPosition = 0.0
    lateinit var motor: DcMotorEx

    abstract val obstacle: Target?
    abstract val targets: List<Target>
    val pidController = SimulatorPIDController(pidParams)

    init {
        if (externalGearRatio < 0) {
            throw IllegalArgumentException("Gear ratio must be positive")
        } else if (externalGearRatio == 0.0) {
            throw IllegalArgumentException("Gear ratio cannot be zero use 1 if not geared")
        }
        if (externalEncoder != null) { // if using an external encoder, the motor gear ratio is 1 as nothing is geared past that
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
        motor.direction = motorDirection
        motor.power = 0.0
        externalEncoder?.init(hardwareMap)

        reset()
    }


    abstract fun run(targetIndex: Int)

    fun reset(){
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
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

    private fun getStallTorque(unit: TorqueUnit): Double {
        motorSpecs.stallTorque.to(unit)
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

    fun getPIDFController(): SimulatorPIDController {
        return pidController
    }

    /**
     * Find the motors torque
     * @param power The power applied to the Motor, derived from the PIDF Controller
     */
    fun calculateTmotor(power: Double, unit: TorqueUnit): Double {
        return calculateTmotor(power, systemConstants.frictionRPM, unit)
    }

    /**
     * Finding the Motor Torque based on the Systems Constants.
     * This function will need to be ran in the Gravity OpMode, so it must take the constants as parameters
     * @see Hardware.Motor Motor being used
     * @param actualRPM Non-theoretical RPM, tested through Friction OpMode
     * @param power Motor Power
     */

    fun calculateTmotor(power: Double, actualRPM: Double, unit: TorqueUnit): Double {
        try {
            require(power in -1.0..1.0) //obviously works
        }catch (_: IllegalArgumentException){
            DataLogger.instance.logError("Power must be between -1 and 1")

        }
        //friction influenced max power
        val friction = actualRPM / getRPM()

        return getStallTorque(unit) * friction * power
    }

    @JvmOverloads
    open fun targetReached(target: Double, accuracy: Double? = null): Boolean {
        return true
    }

    open fun getCurrent(currentUnit: CurrentUnit): Double {
        return motor.getCurrent(currentUnit)
    }

    fun fromAngleToTicks(angle: Double): Double {
        val ticksPerRotation = this.motorSpecs.encoderTicksPerRotation
        return (angle / (2 * Math.PI)) * ticksPerRotation
    }

    fun fromTicksToAngle(ticks: Double): Double {
        val ticksPerRotation = this.motorSpecs.encoderTicksPerRotation
        return (ticks / ticksPerRotation) * 2 * Math.PI
    }

    abstract fun findPosition(): Double
}
