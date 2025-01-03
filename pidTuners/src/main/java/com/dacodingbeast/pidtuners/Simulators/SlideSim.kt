package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import kotlin.math.abs

enum class Direction{
    EXTENDING, RETRACTING
}

class SlideSim(override var motor: Motors, override val targetIndex: Int):SimulatorStructure(motor,targetIndex) {

    override fun updateSimulator(): SimulatorData {
        var target = motor.targets[targetIndex]

        val calculate = pidController.calculate(target, motor.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = motor.calculateTmotor(controlEffort)

        val acceleration = motorTorque / motor.systemConstants.Inertia
        velocity += acceleration * Dt

        target = SlideRange.fromTicks(target.start + velocity * Dt, target.stop)

        return SimulatorData(target.start, controlEffort, error, velocity)
    }

    override val acceptableError = 3.0
    override val acceptableVelocity = 1.0
    override val badAccuracy = abs(error) * 1000
    override val badVelocity = abs(velocity) *20

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity else 0.0)
    }
}