package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import kotlin.math.abs

class SlideSim(override var motor: Motors, override val targetIndex: Int) :
    SimulatorStructure(motor, targetIndex) {



    val slideMotor = motor as SlideMotor
    val mass= (slideMotor.systemConstants as SlideSystemConstants ).effectiveMass

    override fun updateSimulator(): SimulatorData {
        var target = slideMotor.targets[targetIndex]

        val calculate = pidController.calculate(target, slideMotor.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = TorqueUnit.KILOGRAM_CENTIMETER.convert(slideMotor.calculateTmotor(controlEffort), TorqueUnit.NEWTON_METER);

        val diaIn = slideMotor.spoolDiameter// inches
        val radiusOfPulley = (diaIn/2) * 0.0254

        val linearAccelMeters = (motorTorque/radiusOfPulley)/mass
        val linearAccelIN = linearAccelMeters * 1/0.0254
        velocity += linearAccelIN * Dt

        val updatedExtension = target.start + velocity * Dt + 0.5 * linearAccelIN * Dt * Dt

        target = SlideRange.fromInches(updatedExtension, target.stop,slideMotor)

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