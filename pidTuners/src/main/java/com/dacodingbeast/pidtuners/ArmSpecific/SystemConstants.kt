package com.dacodingbeast.pidtuners.ArmSpecific

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
    val RPM: Double = motor.getSpecs().rpm,
) {
    init {
        pso4Arms.SystemConstants = this
    }
}

/**
 * The Constants of a quadratic function that models gravity's effective torque on the Arm
 */
data class GravityModelConstants(val a: Double, val b: Double, val k: Double)