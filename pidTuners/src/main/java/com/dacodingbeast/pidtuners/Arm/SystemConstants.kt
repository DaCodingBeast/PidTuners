package com.dacodingbeast.pidtuners.Arm

import ArmSpecific.pso4Arms
import com.dacodingbeast.pidtuners.HardwareSetup.Motor

/**
 * The Constants needed to Simulate the Arm Mechanism
 * @param RPM The Motors Actual RPM, accounting for the affect of friction
 * @param gravityConstants [gravityConstants]
// * @param motor [motor]
 * @param Inertia The Inertia of the System, measured in the Inertia OpMode
 * @see GravityModelConstants
 * @see Hardware.Motor
 */
data class SystemConstants(
    val motor: Motor,
    val gravityConstants: GravityModelConstants,
    val Inertia: Double,
    val frictionRPM:Double,
) {
    private val rpm:Double
    init {
        pso4Arms.SystemConstants = this
        rpm = motor.getRPM()
    }
}

/**
 * The Constants of a quadratic function that models gravity's effective torque on the Arm
 */
data class GravityModelConstants(val a: Double, val b: Double, val k: Double)