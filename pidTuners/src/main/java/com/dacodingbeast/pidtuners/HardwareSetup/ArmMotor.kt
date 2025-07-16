package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs
import kotlin.math.sin

class ArmMotor private constructor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    systemConstants: PivotSystemConstants,
    override val targets: List<AngleRange>,

    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    externalEncoder: Encoders? = null,
    override val obstacle: AngleRange? = null,
) : Motors(
    name,
    motorDirection,
    motorSpecs,
    systemConstants,
    externalGearRatio,
    pidParams,
    externalEncoder
) {
    class Builder(
        private val name: String,
        private val motorDirection: DcMotorSimple.Direction,
        private val motorSpecs: MotorSpecs,
        private val systemConstants: PivotSystemConstants,
        private val targets: List<AngleRange>
    ) {
        private var externalGearRatio: Double = 1.0
        private var pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0)
        private var externalEncoder: Encoders? = null
        private var obstacle: AngleRange? = null

        fun externalGearRatio(ratio: Double) = apply { this.externalGearRatio = ratio }
        fun pidParams(params: PIDParams) = apply { this.pidParams = params }
        fun externalEncoder(encoder: Encoders?) = apply { this.externalEncoder = encoder }
        fun obstacle(obstacle: AngleRange?) = apply { this.obstacle = obstacle }

        fun build(): ArmMotor {


            return ArmMotor(
                name,
                motorDirection,
                motorSpecs,
                systemConstants,
                targets,
                externalGearRatio,
                pidParams,
                externalEncoder,
                obstacle
            )
        }
    }


    override fun run(targetIndex: Int) {
        val angleRange = AngleRange.fromRadians(findPositionRads(), targets[targetIndex].stop)
        val (_,error) = AngleRange.findDirectionAndError(angleRange, this.obstacle)
        val ticksError = fromAngleToTicks(error)
        val ff = sin(findPositionRads())
        motor.power = pidController.calculate(ticksError, ff).motorPower
    }



    /**
     * To find angle in degrees: Angle.fromRadians
     */
    @JvmOverloads
    fun findPositionRads(inDegrees: Boolean = false): Double {
        val ticks = getCurrentPose()
        val angle = AngleRange.wrap((ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation)))
        return if (inDegrees) angle * 180 / Math.PI else angle
    }

    override fun findPosition(): Double {
        return findPositionRads()
    }


    override fun targetReached(target: Double, accuracy: Double?): Boolean {
        val accurate = accuracy ?: (target * 0.8)
        val angle = AngleRange.fromRadians(findPosition(), target)
        val direction = AngleRange.findMotorDirection(angle, obstacle)
        return abs(AngleRange.findPIDFAngleError(direction, angle)) < Math.toRadians(accurate)
    }



}