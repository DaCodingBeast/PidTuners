package com.dacodingbeast.pidtuners.Simulators

import ArmSpecific.pso4Arms.System.SystemConstants
import ArmSpecific.pso4Slides.System.slideSystemConstants
import CommonUtilities.Models
import android.transition.Slide
import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideRange
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideSystemConstants
import kotlin.math.abs

enum class Direction{
    EXTENDING, RETRACTING
}

class SlideSim(override var target: AngleRange, override val obstacle: List<AngleRange>):SimulatorStructure(target,obstacle) {
    override fun updateSimulator(): SimulatorData {
        val calculate = pidController.calculate(target,obstacle.getOrNull(0))
        val controlEffort = calculate.motorPower

        val motorTorque = Models.calculateTmotor(controlEffort)
        val gravityTorque = Models.gravityTorque(target.start) * if (target.start > 0) -1 else 1

        val torqueApplied = motorTorque + gravityTorque

        val acceleration = torqueApplied / slideSystemConstants.Inertia
        velocity += acceleration * Dt

        target = SlideRange.fromTicks(target.start + velocity * Dt, target.stop).toAngleRange()

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