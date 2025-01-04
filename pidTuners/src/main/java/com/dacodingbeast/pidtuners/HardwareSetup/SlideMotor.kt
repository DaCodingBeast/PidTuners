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
    spoolDiameter: Double,
    systemConstants: ConstantsSuper,
    externalGearRatio: Double = 1.0,
    pidParams: PIDParams = PIDParams(0.0, 0.0, 0.0, 0.0),
    override val targets: List<SlideRange>,
    externalEncoder: Encoder? = null,
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
        ticksPerIn = TicksToInch(spoolDiameter,this).ticksPerInch
        inPerTick = TicksToInch(spoolDiameter,this).inchesPerTick
    }

    fun getExtension(): Double{
        //todo convert ticks to inches
    }

    fun targetReached(target: Double, inchAccuracy:Double = 5.0):Boolean{
        val current = getExtension()
        return current in (target - inchAccuracy)..(target + inchAccuracy)
    }
}