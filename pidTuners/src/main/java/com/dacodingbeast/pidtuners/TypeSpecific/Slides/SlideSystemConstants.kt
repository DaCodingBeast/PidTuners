package com.dacodingbeast.pidtuners.TypeSpecific.Slides

import com.dacodingbeast.pidtuners.HardwareSetup.Motor

/**
 * The Constants needed to Simulate the Arm Mechanism
 * @param RPM The Motors Actual RPM, accounting for the affect of friction
// * @param motor [motor]
 * @param Inertia The Inertia of the System, measured in the Inertia OpMode
 * @see GravityModelConstants
 * @see Hardware.Motor
 */
data class SlideSystemConstants(
    val motor: Motor,
    val Inertia: Double,
    val frictionRPM:Double,
) {
    private val rpm:Double
    init {

        rpm = motor.getRPM()
    }
}

/**
 * The Constants of a quadratic function that models gravity's effective torque on the Arm
 */
data class GravityModelConstants(val a: Double, val b: Double, val k: Double)