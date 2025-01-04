package com.dacodingbeast.pidtuners.HardwareSetup
data class TicksToInch(val spoolDiameter:Double, val motor:Motors){
    val counts = motor.getTicksPerRotation()
    val diameter = spoolDiameter
    val ticksPerInch: Double = counts / (diameter * Math.PI)
    val inchesPerTick: Double = 1.0 / ticksPerInch
}