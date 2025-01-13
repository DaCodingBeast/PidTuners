package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.Target
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
    pidParams: PIDParams = PIDParams(0.0,0.0,0.0,0.0),
    private val externalEncoder: Encoders? = null
) {
    private lateinit var hardwareMap: HardwareMap
    private var startPosition = 0.0
    lateinit var motor: DcMotorEx

    abstract val obstacle: Target?
    abstract val targets: List<Target>
    private val pidController = PIDFcontroller(pidParams)

    init {
        if (externalGearRatio < 0) {
            throw IllegalArgumentException("Gear ratio must be positive")
        }else if (externalGearRatio == 0.0){
            throw IllegalArgumentException("Gear ratio cannot be zero use 1 if not geared")
        }
        if (externalEncoder != null) { // if using an external encoder, the motor gear ratio is 1 as nothing is geared past that
            externalGearRatio = 1.0
            motorSpecs.motorGearRatio = 1.0
        }else { // else, apply the external gear ratio to the motor gear ratio, to find total gear ratio
            motorSpecs.applyGearRatio(externalGearRatio)
        }
        if(targets.isEmpty()){
            throw IllegalArgumentException("Targets List empty, you forgot to add your targets")
        }

    }


    fun init(hardwareMap: HardwareMap, startPosition: Double){
        this.hardwareMap = hardwareMap
        this.startPosition = startPosition
        this.motor = hardwareMap.get(DcMotorEx::class.java, name)
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.direction = motorDirection
        motor.power = 0.0
        externalEncoder?.init(hardwareMap)
    }

    fun run(targetIndex: Int): Double{
        //todo if targets multiple list switch constants based on new target
        motor.power=pidController.calculate(targets[targetIndex], obstacle).motorPower
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
        require(power in -1.0..1.0) //obviously works
        //friction influenced max power
        val friction = actualRPM / getRPM()

        return getStallTorque() * friction * power
    }

    open fun targetReached(target: Double, accuracy: Double?):Boolean {
        return true
    }

    open fun getCurrent(currentUnit: CurrentUnit):Double{
        return motor.getCurrent(currentUnit)
    }

    abstract fun findPosition(): Double

    fun findPositionUnwrapped(): Double {
        val ticks = getCurrentPose()
        val angle = (ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation))
        return angle
    }

}
