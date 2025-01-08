package com.dacodingbeast.pidtuners.HardwareSetup

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.ConstantsSuper
import com.dacodingbeast.pidtuners.Simulators.AngleRange
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

    var ticksPerIn :Double = 1.0
    private var inPerTick:Double = 1.0

    init {
        calculateInPerTick()
    }

    fun calculateInPerTick(){
        ticksPerIn = TicksToInch(spoolDiameter,this).ticksPerInch
        inPerTick = TicksToInch(spoolDiameter,this).inchesPerTick
    }

    override fun findPosition(): Double {
    return getCurrentPose() * inPerTick
}

    override fun targetReached(target: Double, accuracy: Double): Boolean {
        val current = findPosition()
        return current in (target - accuracy)..(target + accuracy)
    }
}