package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import kotlin.math.abs

class SlideSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {

    private val slideMotor = motor as SlideMotor
    private val mass= (slideMotor.systemConstants as SlideSystemConstants ).effectiveMass

    override fun updateSimulator(): SimulatorData {
        val target = slideMotor.targets[targetIndex]

        // Update current position based on velocity
        currentPosition += velocity * Dt
        
        // Create current position object for PID calculation
        val currentPositionObj = SlideRange.fromInches(currentPosition, target.stop, slideMotor)

        val calculate = pidController.calculate(currentPositionObj, slideMotor.obstacle)
        val controlEffort = calculate.motorPower
        error = calculate.error

        val motorTorque = slideMotor.calculateTmotor(controlEffort, TorqueUnit.NEWTON_METER);

        val spoolRadius: Double = slideMotor.spoolDiameter * 0.0254 / 2.0 // meters
        val linearForce = motorTorque / spoolRadius

        val linearAccel = (linearForce/mass)* 39.3701 // linear accel in inches

        velocity += linearAccel * Dt

        return SimulatorData(currentPosition, controlEffort, error, velocity)
    }

    override val acceptableError = 3.0 //inches
    override val acceptableVelocity = 1.0
    override fun badAccuracy() = abs(error) * 1000
    override fun badVelocity() = abs(velocity) * 10

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy() else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity() else 0.0)
    }
}