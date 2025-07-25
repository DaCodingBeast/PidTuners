package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.HardwareSetup.PIDParams
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.utilities.MathFunctions.TicksToInch
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.sin

class SlideMotor private constructor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    systemConstants: SlideSystemConstants,
    val spoolDiameter: Double,
    override val targets: List<SlideRange>,

    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    externalEncoder: Encoders? = null,
    override val obstacle: SlideRange? = null,

    ) : Motors(
    name,
    motorDirection,
    motorSpecs,
    systemConstants,
    externalGearRatio,
    pidParams,
    externalEncoder
) {

    class Builder(private val name: String,
                  private val motorDirection: DcMotorSimple.Direction,
                  private val motorSpecs: MotorSpecs,
                  private val systemConstants: SlideSystemConstants,
                  private val spoolDiameter: Double,
                  private val targets: List<SlideRange>){

        //default values
        private var externalGearRatio: Double = 1.0
        private var pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0)
        private var externalEncoder: Encoders? = null
        private var obstacle: SlideRange? = null
        private var inIn = false
        fun externalGearRatio(ratio: Double) = apply { this.externalGearRatio = ratio }
        fun pidParams(params: PIDParams) = apply { this.pidParams = params }
        fun pidParams(p: Double, i: Double, d: Double, f: Double) = apply { this.pidParams = PIDParams(p, i, d, f) }
        fun externalEncoder(encoder: Encoders?) = apply { this.externalEncoder = encoder }
        fun obstacle(obstacle: SlideRange?) = apply { this.obstacle = obstacle }

        /**
         * Converts to inches, this can be used if needed but is automatic
         */
        private fun fromInches():List<SlideRange> {
            apply {
                var newTargets = mutableListOf<SlideRange>()
                this.targets.forEach {
//                    DataLogger.instance.logDebug("Converting slideRange to inches")
                    newTargets.add(
                        it.toInches(
                            SlideMotor(
                                name,
                                motorDirection,
                                motorSpecs,
                                systemConstants,
                                spoolDiameter,
                                targets,
                                externalGearRatio,
                                pidParams,
                                externalEncoder,
                                obstacle
                            )
                        )
                    )
                }
                this.inIn = true
                return newTargets
            }
        }
        fun build(): SlideMotor {
            require(spoolDiameter>0)
            return SlideMotor(
                name,
                motorDirection,
                motorSpecs,
                systemConstants,
                spoolDiameter,
                if(targets[0].unit == DistanceUnit.TICKS || !inIn) fromInches() else targets,
                externalGearRatio,
                pidParams,
                externalEncoder,
                obstacle
            )
        }
    }

    val conversions = TicksToInch(spoolDiameter, this)

//    override fun run(targetIndex: Int){
//        val error = targets[targetIndex].stop*conversions.ticksPerInch - getCurrentPose()
//        motor.power = pidController.calculate(error, 0.0).motorPower
//    }


    override fun run(targetIndex: Int){
        val error = SlideRange.fromTicks(targets[targetIndex].stop , getCurrentPose() / conversions.ticksPerInch)
        motor.power = pidController.calculate(error, null).motorPower
    }


    override fun findPosition(): Double { // returns inches
        return getCurrentPose() * conversions.inchesPerTick
    }

    /**
     * Checks accuracy in TICKS
     * @param target target in inches
     */
    override fun targetReached(target: Double, accuracy: Double?): Boolean { // all in ticks
        val target2 =target* conversions.ticksPerInch
        val accurate = accuracy ?: 50.0
        val current = getCurrentPose() // in ticks
        return current in (target2 - accurate)..(target2 + accurate)
    }
}