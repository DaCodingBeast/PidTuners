package com.dacodingbeast.pidtuners.Constants

import ArmSpecific.ArmSim
import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

/**
 * The Constants needed to Simulate the Arm Mechanism
 * @param RPM The Motors Actual RPM, accounting for the affect of friction
 * @param gravityConstants [gravityConstants]
// * @param motor [motor]
 * @param Inertia The Inertia of the System, measured in the Inertia OpMode
 * @see GravityModelConstants
 * @see Hardware.Motor
 */
data class PivotSystemConstants(
    override val Inertia: Double,
    override val frictionRPM:Double,
    val gravityConstants: GravityModelConstants,
) : ConstantsSuper(Inertia, frictionRPM)

/**
 * The Constants of a quadratic function that models gravity's effective torque on the Arm
 */
data class GravityModelConstants(val a: Double, val b: Double, val k: Double){
    /**
     * Preforming the mathematical model using the Constants to Find Gravity Torque
     * @see GravityModelConstants
     * @param angle Absolute value of Systems current angle
     */
    fun gravityTorque(angle: Double): Double {
        require(angle in -PI ..PI)// obviously Works

        val angleAbs = abs(angle)

        //Its a parabola created by Desmos based on given input
        return (a * (angleAbs - b).pow(
            2
        ) + k)
    }
}