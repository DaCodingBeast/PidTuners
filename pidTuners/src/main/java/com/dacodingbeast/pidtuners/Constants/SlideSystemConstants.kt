package com.dacodingbeast.pidtuners.Constants

data class SlideSystemConstants(
    override val Inertia: Double,
    override val frictionRPM: Double,
) : ConstantsSuper(Inertia, frictionRPM)
