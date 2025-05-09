package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
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
        targetRange = SlideRange(0.0, 10.0)

        motor = SlideMotor.Builder(
            name = "",
            motorDirection = DcMotorSimple.Direction.FORWARD,
            motorSpecs = Hardware.YellowJacket.RPM223,
            systemConstants = SlideSystemConstants(1.0, 220.0),
            spoolDiameter = 5.0,
            targets = listOf(targetRange)
        ).build()

        slideSim = SlideSim(motor, 0)
    }

    @Test
    fun testPunishSimulator_withAcceptableValues() {
        slideSim.error = 2.0
        slideSim.velocity = 0.5

        val punishment = slideSim.punishSimulator()
        println(punishment)
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
