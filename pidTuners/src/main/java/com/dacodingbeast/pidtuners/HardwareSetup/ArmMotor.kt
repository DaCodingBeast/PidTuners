package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs

//todo fix order of Motors paramters
class ArmMotor @JvmOverloads constructor(
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
    /**
     * To find angle in degrees: Angle.fromRadians(
     */
    override fun findPosition(): Double {
        val ticks = getCurrentPose()
        return AngleRange.wrap((ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation)))
    }

    fun findPosition(inDegrees: Boolean = false): Double {
        val ticks = getCurrentPose()
        val angle = AngleRange.wrap((ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation)))
        return if (inDegrees) angle * 180 / Math.PI else angle
    }

    override fun targetReached(target: Double, accuracy: Double?): Boolean {
        val accurate = accuracy ?: Math.toRadians(15.0)
        val angle = AngleRange.fromRadians(findPosition(), target)
        val direction = AngleRange.findMotorDirection(angle, obstacle)
        return abs(AngleRange.findPIDFAngleError(direction, angle)) < Math.toRadians(accurate)
    }
}