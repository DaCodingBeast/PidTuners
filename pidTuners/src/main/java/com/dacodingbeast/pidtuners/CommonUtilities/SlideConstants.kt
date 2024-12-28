package com.dacodingbeast.pidtuners.CommonUtilities

import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideRange
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideSystemConstants

data class SlideConstants (
    val motor: Motor,
    val target: SlideRange,
    val slideSystemConstants: SlideSystemConstants,
    val obstacle: SlideRange,
//    val ticksToIn:Double,
    )