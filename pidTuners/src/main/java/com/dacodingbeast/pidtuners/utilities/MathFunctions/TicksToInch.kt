package com.dacodingbeast.pidtuners.utilities.MathFunctions

import com.dacodingbeast.pidtuners.HardwareSetup.Motors

data class TicksToInch(val spoolDiameter: Double, val motor: Motors) {
    val counts = motor.getTicksPerRotation()
    val diameter = spoolDiameter
    val ticksPerInch: Double = counts / (diameter * Math.PI)
    val inchesPerTick: Double = 1.0 / ticksPerInch
}