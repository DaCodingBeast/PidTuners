package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.armDirection
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.simulatorType
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.dacodingbeast.pidtuners.Simulators.Target
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

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

//todo jvm overload so no null

class Motor(
    val name: String,
    private var motorDirection: DcMotorSimple.Direction,
    var specs: MotorSpecs,
    private var externalGearRatio: Double = 1.0,
    private val encoder: Encoder?
) {
    private var slideParams: PIDParams? = null
    private var pivotParams: PIDParams? = null
    private var pidFcontroller: PIDFcontroller? = null
    private val simulationType: SimulatorType = simulatorType

    init {
        when (simulationType){
            SimulatorType.SlideSimulator -> slideParams = PIDParams(0.0,0.0,0.0,0.0)
            SimulatorType.ArmSimulator -> pivotParams = PIDParams(0.0,0.0,0.0,0.0)
        }
        pidFcontroller = if (slideParams != null) PIDFcontroller(slideParams!!) else PIDFcontroller(pivotParams!!)
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

    fun runMotor(target: Target){
        pidFcontroller!!.params = if (slideParams != null) slideParams!! else pivotParams!!
        motor.power=pidFcontroller!!.calculate(target).motorPower
    }

    fun sendParams(params: PIDParams){
        if (slideParams != null) slideParams = params else pivotParams = params
    }

    fun getCurrentPose(): Double {
        return encoder?.getCurrentPosition()?.toDouble() ?: motor.currentPosition.toDouble()
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

    /**
     * Find the motors torque
     * @param power The power applied to the Motor, derived from the PIDF Controller
     */
    fun calculateTmotor(power: Double): Double {
        return calculateTmotor(power,PSO_Optimizer.constants.systemSpecific.frictionRPM)
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

    /**
     * Check if Angle Target has been relatively reached, so user can change their own custom states
     * @param degreeAccuracy Angle Accuracy for system to return true In Degrees
     */

    fun targetReached(endAngle:Double,degreeAccuracy: Double = 5.0): Boolean{
        val angleRange = AngleRange.fromRadians(findAngle(), endAngle)
        val direction  = armDirection
        return (abs(AngleRange.findPIDFAngleError(direction, angleRange)) < Math.toRadians(degreeAccuracy))
    }
    var integral: Double = 0.0
    var prevError: Double = 0.0


    fun userCalculate(
        position: Target,
        loopTime: Double
    ): Double {
        val params:PIDParams = if (slideParams != null) slideParams!! else pivotParams!!

        var ff=0.0
        val error = when(position){
            is AngleRange -> {
                val direction = armDirection
                ff = if(position.start>0 ) max(0.0, sin(position.start)) * params.kf else min(0.0, sin(position.start)) * params.kf
                AngleRange.findPIDFAngleError(direction, position)
            }
            is SlideRange ->{
                position.stop - position.start
            }

        }

        integral += (error * loopTime)

        val derivative = (error - prevError) / loopTime
        prevError = error

        return ((derivative * params.kd + integral * params.ki + error * params.kp) + ff).coerceIn(
            -1.0,
            1.0
        )
    }

}