import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PSOOptimizerTest {
    val motor = ArmMotor("",
        DcMotorSimple.Direction.FORWARD,
        Hardware.YellowJacket.RPM223,
        PivotSystemConstants(1.0,220.0,  GravityModelConstants(1.0,2.0,3.0)),
        targets =  listOf(AngleRange.fromRadians(0.0,1.0)),
        obstacle = null, externalEncoder = null
    )
    @Test
    fun `test PSO_Optimizer initialization`() {
        // Mock or create necessary objects
        val motor = motor // Replace with an actual or mocked Motors object
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Check that particles are initialized correctly
        assertNotNull(optimizer.particles)
        assertEquals(100000, optimizer.particles.size) // Ensure the swarm size matches
    }

    @Test
    fun `test global best particle initialization`() {
        val motor = motor
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Ensure gBestParticle is not null and initialized to the first particle
        val globalBest = optimizer.getBest()
        assertNotNull(globalBest)
        assertEquals(optimizer.particles[0], globalBest)
    }

    @Test
    fun `test update improves global best`() {
        val motor = motor
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Mock or simulate initial global best fitness
        val initialBestResult = optimizer.getBest().bestResult

        // Run updates
        optimizer.update(10)

        // Ensure global best fitness improves (or remains the same if optimal)
        val updatedBestResult = optimizer.getBest().bestResult
        assertTrue(updatedBestResult <= initialBestResult)
    }

    @Test
    fun `test particle velocities are updated correctly`() {
        val motor = motor
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Capture initial particle states
        val initialVelocities = optimizer.particles.map { it.velocity }

        // Run a single update
        optimizer.update(1)

        // Ensure velocities are updated
        optimizer.particles.forEachIndexed { index, particle ->
            val initialVelocity = initialVelocities[index]
            assertTrue(particle.velocity != initialVelocity)
        }
    }

    @Test
    fun `test global best is consistent across updates`() {
        val motor = motor
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Run updates
        optimizer.update(10)

        // Ensure the global best is consistent and valid
        val globalBest = optimizer.getBest()
        assertNotNull(globalBest)
        assertTrue(optimizer.particles.contains(globalBest))
    }

    @Test
    fun `test global best after multiple updates`() {
        val motor = motor
        val parameterRanges = arrayListOf(
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0),
            Ranges(0.0, 1.0)
        )
        val optimizer = PSO_Optimizer(parameterRanges, 1.0, motor, 0)

        // Run updates in multiple steps
        optimizer.update(5)
        val intermediateBest = optimizer.getBest()
        optimizer.update(5)
        val finalBest = optimizer.getBest()

        // Ensure global best improves or remains the same
        assertTrue(finalBest.bestResult <= intermediateBest.bestResult)
    }
}
