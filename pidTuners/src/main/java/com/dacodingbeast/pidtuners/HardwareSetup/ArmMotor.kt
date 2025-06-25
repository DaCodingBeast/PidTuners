package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs

class ArmMotor private constructor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    systemConstants: ConstantsSuper,
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
        private val systemConstants: ConstantsSuper,
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
        val accurate = accuracy ?: Math.toRadians(15.0)
        val angle = AngleRange.fromRadians(findPosition(), target)
        val direction = AngleRange.findMotorDirection(angle, obstacle)
        return abs(AngleRange.findPIDFAngleError(direction, angle)) < Math.toRadians(accurate)
    }



}