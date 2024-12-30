package com.dacodingbeast.pidtuners.Constants

import com.dacodingbeast.pidtuners.HardwareSetup.Motor

data class SlideSystemConstants(
    override val Inertia: Double,
    override val frictionRPM:Double,
): ConstantsSuper(Inertia, frictionRPM)
