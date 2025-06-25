package com.dacodingbeast.pidtuners.Mathematics

import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.math.PI

class SlideRangeTests {
    val slideMotor: SlideMotor = SlideMotor.Builder(
        "pivot", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM117,
        SlideSystemConstants(0.0,0.0),1.0,emptyList()
    ).build()
    @Test
    fun testSlideRangeNewConstructs(){
        val range = SlideRange.fromCM(0.0,2.5,slideMotor)
        assertEquals(0.0,range.start)
        assertNotEquals(2.5,range.stop)
        assertEquals(0.0,range.start)
        assertEquals(2.5/2.54,range.stop)
    }
}
