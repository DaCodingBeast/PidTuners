package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.utilities.MathFunctions.TicksToInch
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotorSimple

class SlideMotor private constructor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    systemConstants: ConstantsSuper,
    spoolDiameter: Double,
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
                  private val systemConstants: ConstantsSuper,
                  private val spoolDiameter: Double,
                  private val targets: List<SlideRange>){

        //default values
        private var externalGearRatio: Double = 1.0
        private var pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0)
        private var externalEncoder: Encoders? = null
        private var obstacle: SlideRange? = null
        fun externalGearRatio(ratio: Double) = apply { this.externalGearRatio = ratio }
        fun pidParams(params: PIDParams) = apply { this.pidParams = params }
        fun pidParams(p: Double, i: Double, d: Double, f: Double) = apply { this.pidParams = PIDParams(p, i, d, f) }
        fun externalEncoder(encoder: Encoders?) = apply { this.externalEncoder = encoder }
        fun obstacle(obstacle: SlideRange?) = apply { this.obstacle = obstacle }
        fun build(): SlideMotor {
            require(systemConstants is SlideSystemConstants)
            require(spoolDiameter>0)
            return SlideMotor(
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
        }
    }

    override fun run(targetIndex: Int){
        val range = SlideRange.fromTicks(findPosition(),targets[targetIndex].stop)
        motor.power = pidController.calculate(range, obstacle).motorPower
    }

    var conversions = TicksToInch(spoolDiameter, this)


    override fun findPosition(): Double { // returns ticks
        return getCurrentPose()
    }

    /**
     * Checks accuracy in inches
     */
    override fun targetReached(target: Double, accuracy: Double?): Boolean {
        val accurate = accuracy ?: 50.0
        val current = findPosition() // in ticks
        return current in (target - accurate)..(target + accurate)
    }
}