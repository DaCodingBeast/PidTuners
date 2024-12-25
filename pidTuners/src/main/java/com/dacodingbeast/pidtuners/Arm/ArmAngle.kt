package ArmSpecific

import com.dacodingbeast.pidtuners.CommonUtilities.Hardware
import com.dacodingbeast.pidtuners.Mathematics.AngleRange

class ArmAngle (val motor: Hardware.Motor, private val angleOffset: Double) {
    fun findAngle(encoder: Int): Double {
        val angle = AngleRange.wrap(encoder.toDouble() * (2 * Math.PI / motor.getSpecs().encoderTicksPerRotation))
        return AngleRange.wrap(angle + angleOffset)
    }
}