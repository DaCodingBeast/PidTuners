package com.dacodingbeast.pidtuners.HardwareSetup.torque

data class StallTorque(var value: Double, var unit: TorqueUnit) {
    constructor(value: Double) : this(value, TorqueUnit.NEWTON_METER)

    fun to(targetUnit: TorqueUnit) {
        value = unit.convert(value, targetUnit)
        unit = targetUnit
    }

    override fun toString(): String {
        return "$value ${unit.symbol}"
    }
}