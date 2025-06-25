package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import kotlin.math.abs

class SlideSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {
        val motors = motor as SlideMotor

    override fun updateSimulator(): SimulatorData {
        var target = motors.targets[targetIndex] as SlideRange

        val calculate = pidController.calculate(target, motors.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = motors.calculateTmotor(controlEffort)

        //todo look at this in depth
        val acceleration = motorTorque / motors.systemConstants.Inertia
        velocity += acceleration * Dt

        val angleStart = fromInchesToAngle(target.start)
        val newAngle = angleStart + Dt * velocity

        val currentPose = fromAngleToInches(newAngle)
        val stop = target.stop


        target = SlideRange.fromInches(currentPose, stop,motor as SlideMotor)

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

    private fun fromInchesToAngle(Inches: Double): Double {
        return motors.fromTicksToAngle(motors.fromInchesToTicks(Inches))
    }

    private fun fromAngleToInches(Angle: Double): Double {
        return motors.fromTicksToInches(motors.fromAngleToTicks(Angle))
    }
}