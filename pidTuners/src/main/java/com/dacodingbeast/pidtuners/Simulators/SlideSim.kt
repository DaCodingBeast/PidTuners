package com.dacodingbeast.pidtuners.Simulators

import android.transition.Slide
import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import kotlin.math.abs

enum class Direction{
    EXTENDING, RETRACTING
}

class SlideSim(override var target: SlideRange):SimulatorStructure(target) {

    private val c = constants.systemSpecific as SlideSystemConstants

    override fun updateSimulator(): SimulatorData {
        val calculate = pidController.calculate(target,constants.motor.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = constants.motor.calculateTmotor(controlEffort)

        val torqueApplied = motorTorque

        val acceleration = torqueApplied / c.Inertia
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