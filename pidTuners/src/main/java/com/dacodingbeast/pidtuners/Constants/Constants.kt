package com.dacodingbeast.pidtuners.Constants

import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.dacodingbeast.pidtuners.Simulators.Target

data class Constants(
    val motor: Motor,
    val angles: List<Target>,
    val obstacle: List<Target>,
    val systemSpecific: ConstantsSuper
)