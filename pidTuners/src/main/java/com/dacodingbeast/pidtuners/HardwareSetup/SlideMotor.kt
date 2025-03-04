package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.MathFunctions.TicksToInch
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.qualcomm.robotcore.hardware.DcMotorSimple

class SlideMotor(
    name: String,
    motorDirection: DcMotorSimple.Direction,
    motorSpecs: MotorSpecs,
    val spoolDiameter: Double,
    systemConstants: ConstantsSuper,
    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    override val targets: List<SlideRange>,
    private val externalEncoder: Encoders? = null,
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


    var ticksPerIn: Double = 1.0
    private var inPerTick: Double = 1.0

    init {
        calculateInPerTick()
    }

    fun calculateInPerTick() {
        ticksPerIn = TicksToInch(spoolDiameter, this).ticksPerInch
        inPerTick = TicksToInch(spoolDiameter, this).inchesPerTick
    }

    override fun findPosition(): Double {
        return getCurrentPose() * inPerTick
    }

    override fun targetReached(target: Double, accuracy: Double?): Boolean {
        val accurate = accuracy ?: 50.0
        val current = findPosition()
        return current in (target - accurate)..(target + accurate)
    }
}