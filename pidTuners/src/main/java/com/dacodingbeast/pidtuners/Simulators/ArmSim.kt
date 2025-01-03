package ArmSpecific

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
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

class ArmSim(override var target: AngleRange) :
    SimulatorStructure(target) {

    private val armSpecific = constants.systemSpecific as PivotSystemConstants

    /**
     * This function calculates the sum of two integers.
     * @return Arms Angle, Error, and motor power
     */

    override fun updateSimulator(): SimulatorData {
        val calculate = pidController.calculate(target, constants.motor.obstacle)
        val controlEffort = calculate.motorPower

        val motorTorque = constants.motor.calculateTmotor(controlEffort)
        val gravityTorque = armSpecific.gravityConstants.gravityTorque(abs(target.start)) * if (target.start > 0) -1 else 1

        val torqueApplied = motorTorque + gravityTorque

        val angularAcceleration = torqueApplied / armSpecific.Inertia
        velocity += angularAcceleration * Dt

        target = AngleRange.fromRadians(
            AngleRange.wrap(target.start + velocity * Dt),
            target.stop
        )

        return SimulatorData(target.start, controlEffort, error, velocity)
    }

    override val acceptableError = Math.toRadians(3.0)
    override val acceptableVelocity = 1.0
    override val badAccuracy = abs(error) * 1000
    override val badVelocity = abs(velocity) *20

    override fun punishSimulator(): Double {
        return (if (error >= acceptableError) badAccuracy else 0.0) +
                (if (velocity >= acceptableVelocity) badVelocity else 0.0)
    }

}
