package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.Simulators.SlideSim
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SlideSimTest {

    private lateinit var motor: Motors
    private lateinit var slideSim: SlideSim
    private lateinit var targetRange: SlideRange

    @Before
    fun setUp() {
        // Mock motor and SlideRange
        motor = SlideMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            5.0,
            SlideSystemConstants(1.0, 220.0),
            targets = listOf(SlideRange.fromInches(0.0, 10.0))
        )
        targetRange = SlideRange.fromInches(0.0, 10.0)

        // Create SlideSim instance
        slideSim = SlideSim(motor, 0)
    }

    @Test
    fun testPunishSimulator_withAcceptableValues() {
        slideSim.error = 2.0
        slideSim.velocity = 0.5

        val punishment = slideSim.punishSimulator()

        assertEquals(0.0, punishment, 0.1)
    }

    @Test
    fun testPunishSimulator_withExceedingValues() {
        slideSim.error = 4.0
        slideSim.velocity = 2.0

        val punishment = slideSim.punishSimulator()

        val expectedPunishment = slideSim.badAccuracy + slideSim.badVelocity
        assertEquals(expectedPunishment, punishment, 0.1)
    }
}
