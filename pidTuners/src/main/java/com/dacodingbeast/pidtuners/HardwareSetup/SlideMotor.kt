package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.utilities.MathFunctions.TicksToInch
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.qualcomm.robotcore.hardware.DcMotorSimple

class SlideMotor @JvmOverloads constructor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    private val spoolDiameter: Double,
    systemConstants: ConstantsSuper,
    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    override val targets: List<SlideRange>,
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
    constructor(
        name: String,
        motorDirection: DcMotorSimple.Direction,
        motorSpecs: MotorSpecs,
        systemConstants: ConstantsSuper,
        spoolDiameter: Double,
        externalGearRatio: Double = 1.0,
        pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
        target: List<SlideRange>,
    ) : this(
        name,
        motorDirection,
        motorSpecs,
        spoolDiameter,
        systemConstants,
        externalGearRatio,
        pidParams,
        target,
        null,
        null
    )

    constructor(
        name: String,
        motorDirection: DcMotorSimple.Direction,
        motorSpecs: MotorSpecs,
        systemConstants: ConstantsSuper,
        spoolDiameter: Double,
        targets: List<SlideRange>,
    ) : this(
        name,
        motorDirection,
        motorSpecs,
        spoolDiameter,
        systemConstants,
        1.0,
        PIDParams(0.0, 0.0, 0.0, 0.0),
        targets,
        null,
        null,
    )


    var conversions = TicksToInch(spoolDiameter, this)


    override fun findPosition(): Double {
        return getCurrentPose() * conversions.inchesPerTick
    }

    /**
     * Checks accuracy in inches
     */
    override fun targetReached(target: Double, accuracy: Double?): Boolean {
        val accurate = accuracy ?: 1.0
        val current = findPosition()
        return current in (target - accurate)..(target + accurate)
    }

    fun fromInchesToTicks(value: Double): Double {
        return value * this.conversions.ticksPerInch
    }

    fun fromTicksToInches(value: Double): Double {
        return value / this.conversions.ticksPerInch
    }

    fun findPositionUnwrapped(): Double {
        val ticks = getCurrentPose()
        val angle = (ticks * (2 * Math.PI / motorSpecs.encoderTicksPerRotation))
        return angle
    }
}