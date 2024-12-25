package ArmSpecific

import ArmSpecific.pso4Arms.System.SystemConstants
import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import CommonUtilities.Models
import CommonUtilities.PIDFParams
import CommonUtilities.PIDFcontroller
import com.dacodingbeast.pidtuners.Simulators.ArmSimData
import kotlin.math.abs



class ArmSim(
    private var angleRange: AngleRange,
    private val badAngleRange: AngleRange?,
    params: PIDFParams
) {
    var angularVelocity = 0.0
    private var angularAcceleration = 0.0
    private var shoulder = PIDFcontroller(params)
    private var Ttotal = 0.0

    /**
     * This function calculates the sum of two integers.
     * @return Arms Angle, Error, and motor power
     */
    fun updateSim(): ArmSimData {
        val calculate = shoulder.calculate(angleRange, badAngleRange)
        val controlEffort = calculate.motorPower

        /**
         * @see Models.calculateTmotor
         * @see Models.gravityTorque
         */

        Ttotal = if(angleRange.start >0 ) Models.calculateTmotor(controlEffort) - Models.gravityTorque(abs(angleRange.start))
        else Models.calculateTmotor(controlEffort) + Models.gravityTorque(abs(angleRange.start))


        angularAcceleration = Ttotal / SystemConstants.Inertia
        angularVelocity += angularAcceleration * Dt
        angleRange = AngleRange.fromRadians(
            AngleRange.wrap(angleRange.start + angularVelocity * Dt),
            angleRange.target
        )
//        println("d: $direction  c: $controlEffort  v: $angularVelocity a: ${angleRange.start}")
        return ArmSimData(angleRange, controlEffort, calculate.error)
    }

}