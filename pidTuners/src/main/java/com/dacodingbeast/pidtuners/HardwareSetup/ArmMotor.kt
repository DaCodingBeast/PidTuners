package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs

class ArmMotor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    systemConstants: ConstantsSuper,
    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    override val targets: List<AngleRange>,
    externalEncoder: Encoder? = null,
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
    fun findAngle(inDegrees : Boolean = false): Double {
        val ticks = getCurrentPose()
        val angle = AngleRange.wrap((ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation)))
        return if (inDegrees) angle * 180 / Math.PI else angle
    }

    fun targetReached(target:Double,degreeAccuracy :Double = 5.0):Boolean{
        val angle  = AngleRange.fromRadians(findAngle(), target)
        val direction = AngleRange.findMotorDirection(angle, obstacle)
        return abs(AngleRange.findPIDFAngleError(direction,angle)) < Math.toRadians(degreeAccuracy)
    }
}