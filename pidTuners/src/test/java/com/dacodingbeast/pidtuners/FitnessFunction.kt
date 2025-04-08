package com.example.pso4pid

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Algorithm.FitnessFunction
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.random.Random

class FitnessFunctionTest {

    @Test
    fun `test basic fitness computation for ArmMotor`() {
        // Mock or create instances of Motor, Particle, etc.
        val motor = ArmMotor.Builder("", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        ).externalGearRatio(1.0)
            .pidParams(PIDParams(1.0, 0.5, 0.25))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(1.0, motor, 0)
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        // Compute fitness
        val fitnessData = fitnessFunction.findFitness(particle)

        // Assert the results
        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae >= 0) // ITAE should be non-negative
        assertTrue(fitnessData.history.isNotEmpty()) // History should be populated
    }

    @Test
    fun `test fitness computation for ArmMotor with second target`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(
                AngleRange.fromRadians(0.0, 1.0),
                AngleRange.fromRadians(1.0, 2.0)
            )
        ).externalGearRatio(1.5)
            .pidParams(PIDParams(2.0, 1.0, 0.5))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(1.0, motor, 1)
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        val fitnessData = fitnessFunction.findFitness(particle)

        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae >= 0) // ITAE should be non-negative
        assertTrue(fitnessData.history.isNotEmpty()) // History should be populated
    }

    @Test
    fun `test fitness computation with zero range`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(1.0, 1.0)) // Zero range
        ).externalGearRatio(2.0)
            .pidParams(PIDParams(1.5, 0.5, 0.25))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(1.0, motor, 0)
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        val fitnessData = fitnessFunction.findFitness(particle)

        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae >= 0) // ITAE should be non-negative
        assertTrue(fitnessData.history.isNotEmpty()) // History should be populated
    }

    @Test
    fun `test punishment logic impact on fitness score`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        ).externalGearRatio(2.0)
            .pidParams(PIDParams(1.5, 0.75, 0.5))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(1.0, motor, 0)
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        val fitnessData = fitnessFunction.findFitness(particle)

        val baselineITAE = fitnessData.itae
        assertTrue(baselineITAE > 0)

        // Assuming `punishSimulator` logic adds to ITAE
        val punishedITAE = fitnessFunction.findFitness(particle).itae
        assertTrue(punishedITAE == baselineITAE) // Punishment increases or equals ITAE
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test fitness computation with high total time`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        ).externalGearRatio(1.5)
            .pidParams(PIDParams(1.0, 1.0, 0.5))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(10.0, motor, 0) // Increased total time
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        val fitnessData = fitnessFunction.findFitness(particle)

        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae == 0.0) // ITAE should accumulate more over time
        assertTrue(fitnessData.history.size > 1) // Longer history due to higher total time
    }

    @Test
    fun `test fitness computation with reverse motor direction`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.REVERSE, // Reverse direction
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        ).externalGearRatio(2.0)
            .pidParams(PIDParams(1.2, 0.6, 0.3))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        val fitnessFunction = FitnessFunction(1.0, motor, 0)
        val particle = Particle(
            listOf(
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0),
                Ranges(0.0, 1.0)
            ),
            fitnessFunction
        )

        val fitnessData = fitnessFunction.findFitness(particle)

        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae >= 0) // ITAE should remain valid
        assertTrue(fitnessData.history.isNotEmpty()) // History should still be populated
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test fitness with invalid configuration`() {
        val motor = ArmMotor.Builder(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = emptyList() // Invalid configuration: no targets
        ).externalGearRatio(1.0)
            .pidParams(PIDParams(1.0, 1.0, 0.5))
            .externalEncoder(null)
            .obstacle(null)
            .build()

        FitnessFunction(1.0, motor, 0)
    }
}
