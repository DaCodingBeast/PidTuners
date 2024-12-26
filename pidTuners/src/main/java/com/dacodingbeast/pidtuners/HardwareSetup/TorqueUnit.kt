package com.dacodingbeast.pidtuners.HardwareSetup

enum class TorqueUnit(val symbol: String, private val toNewtonMeterFactor: Double) {
    NEWTON_METER("Nm", 1.0),
    POUND_FEET("lb-ft", 0.737562),
    KILOGRAM_CENTIMETER("kg-cm", 10.1972),
    OUNCE_INCH("oz-in", 141.612);

    fun convert(value: Double, targetUnit: TorqueUnit): Double {
        return value * this.toNewtonMeterFactor / targetUnit.toNewtonMeterFactor
    }
}