package com.dacodingbeast.pidtuners.HardwareSetup.torque

data class StallTorque(var value: Double, var unit: TorqueUnit) {
    fun to(targetUnit: TorqueUnit) {
        value = unit.convert(value, targetUnit)
        unit = targetUnit
    }

    override fun toString(): String {
        return "$value ${unit.symbol}"
    }
}