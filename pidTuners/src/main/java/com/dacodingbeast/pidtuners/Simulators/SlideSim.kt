package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import kotlin.math.abs

class SlideSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {

    override fun updateSimulator(): SimulatorData {
        var target = motor.targets[targetIndex] as SlideRange

        val calculate = pidController.calculate(target, motor.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = motor.calculateTmotor(controlEffort)

        val acceleration = motorTorque / motor.systemConstants.Inertia
        velocity += acceleration * Dt

        val angleStart = motor.fromInchesToAngle(target.start)
        val newAngle = angleStart + Dt * velocity

        val currentPose = motor.fromAngleToInches(newAngle)
        val stop = target.stop


        target = SlideRange(currentPose, stop)

        return SimulatorData(target.start, controlEffort, error, velocity)
    }

    override val acceptableError = 3.0
    override val acceptableVelocity = 1.0
    override val badAccuracy = abs(error) * 1000
    override val badVelocity = abs(velocity) * 20

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity else 0.0)
    }
}