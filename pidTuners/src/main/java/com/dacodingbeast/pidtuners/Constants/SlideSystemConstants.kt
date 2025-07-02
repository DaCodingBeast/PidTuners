package com.dacodingbeast.pidtuners.Constants

data class SlideSystemConstants(
    val effectiveMass: Double,
    override val frictionRPM: Double,
): ConstantsSuper (frictionRPM)