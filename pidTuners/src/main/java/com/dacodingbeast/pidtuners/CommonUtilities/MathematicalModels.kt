package CommonUtilities

import ArmSpecific.Hardware
import ArmSpecific.pso4Arms.System.SystemConstants
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

/**
 * Mathematical Models the system needs
 */
object Models {
    /**
     * Find the motors torque
     * @param power The power applied to the Motor, derived from the PIDF Controller
     */
    fun calculateTmotor(power: Double): Double {
//        require(power in -1.0..1.0) Obviously works

        val maxTorque = SystemConstants.motor.stallTorque
        val actualRPM = SystemConstants.RPM
        val theoreticalRPM = SystemConstants.motor.rpm
        //friction influenced max power
        val friction = actualRPM / theoreticalRPM

        return maxTorque * friction * power
    }

    /**
     * Preforming the mathematical model using the Constants to Find Gravity Torque
     * @see GravityModelConstants
     * @param angle Absolute value of Systems current angle
     */
    fun gravityTorque(angle: Double): Double {
        require(angle in -PI ..PI)// obviously Works

        val angleAbs = abs(angle)

        //Its a parabola created by Desmos based on given input
        return (SystemConstants.gravityConstants.a * (angleAbs - SystemConstants.gravityConstants.b).pow(
            2
        ) + SystemConstants.gravityConstants.k)
    }
    /**
     * Finding the Motor Torque based on the Systems Constants.
     * This function will need to be ran in the Gravity OpMode, so it must take the constants as parameters
     * @see Hardware.Motor Motor being used
     * @param actualRPM Non-theoretical RPM, tested through Friction OpMode
     * @param power Motor Power
     */
    @JvmStatic
    fun calculateTmotor(power: Double, motor: Hardware.Motor, actualRPM: Double): Double {
        require(power in -1.0..1.0) //obviously works
        //friction influenced max power
        val friction = actualRPM / motor.rpm

        return motor.stallTorque * friction * power
    }
}