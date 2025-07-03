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
        var target = slideMotor.targets[targetIndex]

        val calculate = pidController.calculate(target, slideMotor.obstacle)
        val controlEffort = calculate.motorPower
        error = calculate.error

        val motorTorque = slideMotor.calculateTmotor(controlEffort, TorqueUnit.NEWTON_METER);

        val spoolRadius: Double = slideMotor.spoolDiameter * 0.0254 / 2.0 // meters
        val linearForce = motorTorque / spoolRadius

        val linearAccel = (linearForce/mass)/0.0254 // meters

        velocity += linearAccel * Dt

        val updatedExtension = target.start + velocity * Dt + 0.5 * linearAccel * Dt * Dt

        target = SlideRange.fromInches(updatedExtension, target.stop,slideMotor)

        return SimulatorData(target.start, controlEffort, error, velocity)
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