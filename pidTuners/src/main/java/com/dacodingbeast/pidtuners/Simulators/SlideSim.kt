package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import kotlin.math.abs

class SlideSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {
        val motors = motor as SlideMotor

    override fun updateSimulator(): SimulatorData {
        var target = motors.targets[targetIndex]

        val calculate = pidController.calculate(target, motors.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = motors.calculateTmotor(controlEffort)

        //todo look at this in depth
        val acceleration = motorTorque / motors.systemConstants.Inertia
        velocity += acceleration * Dt

        val angleStart =target.start
        val newAngle = angleStart + Dt * velocity

        val currentPose = newAngle
        val stop = target.stop


        target = SlideRange.fromTicks(currentPose, stop,motor as SlideMotor)

        return SimulatorData(target.start, controlEffort, error, velocity)
    }

    override val acceptableError = 3.0 //inches
    override val acceptableVelocity = 1.0
    override val badAccuracy = abs(error) * 1000
    override val badVelocity = abs(velocity) * 20

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity else 0.0)
    }
}