package com.dacodingbeast.pidtuners.Simulators

import com.dacodingbeast.pidtuners.Mathematics.AngleRange

/**
 *The simulation that translates the effect of [PIDFcontroller] on the Arm Angle
 */

class ArmSimData(val armAngle: AngleRange, val motorPower: Double, val error: Double)