package ArmSpecific

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
        var target = armMotor.targets[targetIndex]

        val calculate = pidController.calculate(target, armMotor.obstacle)
        error = calculate.error

        val controlEffort = calculate.motorPower

        val motorTorque = armMotor.calculateTmotor(controlEffort, TorqueUnit.NEWTON_METER)

        val g = (armMotor.systemConstants as PivotSystemConstants).gravityConstants
        val gravityTorque = g.gravityTorque(abs(target.start)) * if (target.start > 0) -1 else 1

        val torqueApplied = motorTorque + gravityTorque

        val angularAcceleration = torqueApplied / armMotor.systemConstants.Inertia
        velocity += angularAcceleration * Dt

        target = AngleRange.fromRadians(
            AngleRange.wrap(target.start + velocity * Dt),
            target.stop
        )

        return SimulatorData(target.start, controlEffort, error, velocity)
    }

    override val acceptableError = Math.toRadians(3.0)
    override val acceptableVelocity = 1.0
    override fun badAccuracy() = abs(error) * 1000
    override fun badVelocity() = abs(velocity) * 20

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy() else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity() else 0.0)
    }

}
