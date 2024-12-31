package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.pidParams
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.simulatorType
import com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.spoolDiameter
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.dacodingbeast.pidtuners.Simulators.Target
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.abs

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
    val obstacle: Target?,
    private val encoder: Encoder?
) {
    private var slideParams: PIDParams? = null
    private var pivotParams: PIDParams? = null
    private var pidFcontroller: PIDFcontroller? = null
    private val simulationType: SimulatorType = simulatorType

    var ticksPerIn :Double = 1.0
    private var inPerTick:Double = 1.0

    init {
        when (simulationType){
            SimulatorType.SlideSimulator -> slideParams = PIDParams(0.0,0.0,0.0,0.0)
            SimulatorType.ArmSimulator -> pivotParams = PIDParams(0.0,0.0,0.0,0.0)
        }

        setParams(pidParams)

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
        ticksPerIn = EncoderMath(spoolDiameter,this).ticksPerInch
        inPerTick = EncoderMath(spoolDiameter,this).inchesPerTick
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
        motor.power=pidFcontroller!!.calculate(target, obstacle).motorPower
    }

    fun setParams(params: PIDParams){
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

    var target: Double = 0.0
    /**
     * Check if Angle Target has been relatively reached, so user can change their own custom states
     * @param degreeAccuracy Angle Accuracy for system to return true In Degrees
     * This will not work if you have an obstacle that is thinner than the given angle
     */

    fun targetReached(degreeAccuracy: Double = 5.0): Boolean{
        when(simulationType){
            SimulatorType.ArmSimulator ->{

                //todo - This will not work if you have an obstacle that is thinner than the given angle

                val angle  = AngleRange.fromRadians(findAngle(), this.target)
                val direction = AngleRange.findMotorDirection(angle, obstacle as AngleRange?)
                return abs(AngleRange.findPIDFAngleError(direction,angle)) < Math.toRadians(degreeAccuracy)
            }
            SimulatorType.SlideSimulator ->{
                val ticksAccuracy = degreeAccuracy*10
                val current = this.getCurrentPose()
                return current in (target - ticksAccuracy)..(target + ticksAccuracy)
            }
        }
    }


}
data class EncoderMath(val spoolDiameter:Double, val motor:Motor){
    val counts = motor.getTicksPerRotation()
    val diameter = spoolDiameter
    val ticksPerInch: Double = counts / (diameter * Math.PI)
    val inchesPerTick: Double = 1.0 / ticksPerInch
}