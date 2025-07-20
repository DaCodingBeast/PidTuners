package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SimulatorData
import com.dacodingbeast.pidtuners.Simulators.SimulatorStructure
import kotlin.math.abs

/**
 * An Enum Class Containing the two Directions the Arm can run in
 */
enum class Direction {
    Clockwise, CounterClockWise
}

class ArmSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {

    /**
     * This function calculates the sum of two integers.
     * @return Arms Angle, Error, and motor power
     */
    private val armMotor = motor as ArmMotor

    override fun updateSimulator(): SimulatorData {
        val target = armMotor.targets[targetIndex]
        
        // Debug: Print current state before update
//        println("=== ArmSim Update ===")
//        println("Current Position: ${Math.toDegrees(currentPosition)}° (${currentPosition} rad)")
//        println("Target Position: ${Math.toDegrees(target.stop)}° (${target.stop} rad)")
//        println("Current Velocity: ${Math.toDegrees(velocity)}°/s (${velocity} rad/s)")
//        println("Current Error: ${Math.toDegrees(error)}° (${error} rad)")
        
        currentPosition += velocity * Dt
        currentPosition = AngleRange.wrap(currentPosition)
        
//        println("New Position after update: ${Math.toDegrees(currentPosition)}° (${currentPosition} rad)")
        
        val currentPositionObj = AngleRange.fromRadians(
            currentPosition,
            target.stop
        )

//        println("Current Position Object - start: ${Math.toDegrees(currentPositionObj.start)}°, stop: ${Math.toDegrees(currentPositionObj.stop)}°")

        val calculate = pidController.calculate(currentPositionObj, armMotor.obstacle)
        error = calculate.error

        val controlEffort = calculate.motorPower
        
//        println("PID Calculation Results:")
//        println("  Control Effort: $controlEffort")
//        println("  Error: ${Math.toDegrees(error)}° (${error} rad)")

        // Calculate motor torque in N⋅m for consistent units
        val motorTorque = armMotor.calculateTmotor(controlEffort, TorqueUnit.NEWTON_METER)
        
//        println("Motor Torque: $motorTorque N⋅m")

        val g = (armMotor.systemConstants as PivotSystemConstants).gravityConstants
        // Gravity torque is in kg-cm, convert to N⋅m to match motor torque
        val gravityTorqueKgCm = g.gravityTorque(abs(currentPosition)) * if (currentPosition > 0) -1 else 1
        val gravityTorque = gravityTorqueKgCm * 0.09807 // Convert kg-cm to N⋅m

//        println("Gravity Torque: $gravityTorque N⋅m")
//        println("Current Angle for gravity calc: ${Math.toDegrees(abs(currentPosition))}°")

        val torqueApplied = motorTorque + gravityTorque
        
//        println("Total Torque Applied: $torqueApplied N⋅m")

        // Inertia should be in kg⋅m², torque in N⋅m, so angular acceleration will be in rad/s²
        val angularAcceleration = torqueApplied / armMotor.systemConstants.Inertia
        velocity += angularAcceleration * Dt
        
//        println("Angular Acceleration: ${Math.toDegrees(angularAcceleration)}°/s² (${angularAcceleration} rad/s²)")
//        println("New Velocity: ${Math.toDegrees(velocity)}°/s (${velocity} rad/s)")
//        println("System Inertia: ${armMotor.systemConstants.Inertia} kg⋅m²")
//        println("Time Step (Dt): $Dt s")
//        println("================================")

        return SimulatorData(currentPosition, controlEffort, error, velocity)
    }

    override val acceptableError = Math.toRadians(3.0)
    override val acceptableVelocity = 1.0
    override fun badAccuracy() = abs(error) * 1000
    override fun badVelocity() = abs(velocity) * 20

    override fun punishSimulator(): Double {
        val accuracyPunishment = if (error >= acceptableError) badAccuracy() else 0.0
        val velocityPunishment = if (velocity >= acceptableVelocity) badVelocity() else 0.0
        val totalPunishment = accuracyPunishment + velocityPunishment
        
//        println("Punishment Calculation:")
//        println("  Error: ${Math.toDegrees(error)}° (acceptable: ${Math.toDegrees(acceptableError)}°)")
//        println("  Velocity: ${Math.toDegrees(velocity)}°/s (acceptable: $acceptableVelocity rad/s)")
//        println("  Accuracy Punishment: $accuracyPunishment")
//        println("  Velocity Punishment: $velocityPunishment")
//        println("  Total Punishment: $totalPunishment")
        
        return totalPunishment
    }

}
