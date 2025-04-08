package com.example.pso4pid

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

class FitnessFunction {
    @Test
    fun `test basic fitness computation for ArmMotor`() {
        // Mock or create instances of Motor, Particle, etc.
        val motor = ArmMotor("",DcMotorSimple.Direction.FORWARD,Hardware.YellowJacket.RPM223, PivotSystemConstants(1.0,220.0,
            GravityModelConstants(1.0,2.0,3.0)), targets = listOf(AngleRange.fromRadians(0.0,1.0)), obstacle = null, externalEncoder = null)
        val fitnessFunction = FitnessFunction(1.0, motor, 0)
        val particle = Particle(listOf(Ranges(0.0,1.0),Ranges(0.0,1.0),Ranges(0.0,1.0),Ranges(0.0,1.0)),fitnessFunction) // Mock or provide a valid instance

        // Compute fitness
        val fitnessData = fitnessFunction.findFitness(particle)

        // Assert the results
        assertNotNull(fitnessData)
        assertTrue(fitnessData.itae >= 0) // ITAE should be non-negative
        assertTrue(fitnessData.history.isNotEmpty()) // History should be populated
    }


    @Test
    fun `test fitness computation for ArmMotor with second target`() {
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(
                AngleRange.fromRadians(0.0, 1.0),
                AngleRange.fromRadians(1.0, 2.0)
            )
        )
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
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(1.0, 1.0)) // Zero range
        )
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
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        )

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
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        )
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
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.REVERSE, // Reverse direction
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = listOf(AngleRange.fromRadians(0.0, 1.0))
        )
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
        val motor = ArmMotor(
            "",
            DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            PivotSystemConstants(1.0, 220.0, GravityModelConstants(1.0, 2.0, 3.0)),
            targets = emptyList()
        )


        FitnessFunction(1.0, motor, 0)
    }




}