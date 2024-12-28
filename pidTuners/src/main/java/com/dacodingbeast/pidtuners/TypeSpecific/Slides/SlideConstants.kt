package com.dacodingbeast.pidtuners.TypeSpecific.Slides

import com.dacodingbeast.pidtuners.HardwareSetup.Motor

data class SlideConstants (
    val motor: Motor,
    val target: SlideRange,
    val slideSystemConstants: SlideSystemConstants,
    val obstacle: SlideRange,
//    val ticksToIn:Double,
    )