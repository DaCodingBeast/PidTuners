package com.dacodingbeast.pidtuners.HardwareSetup

import com.qualcomm.robotcore.hardware.HardwareMap

abstract class Encoders(
    open val name: String,
) {
    open fun init(ahwMap: HardwareMap) {
    }

    open fun getCurrentPosition(): Int {
        return 0
    }
}