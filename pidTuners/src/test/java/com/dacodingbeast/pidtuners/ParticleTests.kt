import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.Algorithm.Vector
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Algorithm.FitnessFunction
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class ParticleTests {

    // Create test ranges
    private val testRanges = listOf(
        Ranges(0.0, 10.0),
        Ranges(0.0, 5.0),
        Ranges(0.0, 2.0)
    )

    // Create a simple motor setup for testing
    private fun createTestMotor(): ArmMotor {
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val gravityConstants = GravityModelConstants(1.0, 0.5, 0.1)
        val systemConstants = PivotSystemConstants(1.0, 50.0, gravityConstants)
        val targets = listOf(AngleRange.fromDegrees(0.0, 90.0))
        
        return ArmMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            targets
        ).build()
    }

    // Create a fitness function for testing
    private fun createFitnessFunction(): FitnessFunction {
        val motor = createTestMotor()
        return FitnessFunction(1.0, motor, 0) // 1 second simulation, target index 0
    }

    @Test
    fun `test Particle initialization`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        assertNotNull(particle)
        assertNotNull(particle.position)
        assertNotNull(particle.velocity)
        assertNotNull(particle.pBestParam)
        
        // Check that position is within ranges
        for (i in testRanges.indices) {
            assertTrue("Position should be within range ${i}", 
                particle.position.particleParams[i] >= testRanges[i].start)
            assertTrue("Position should be within range ${i}", 
                particle.position.particleParams[i] <= testRanges[i].stop)
        }
        
        // Check that velocity is initialized to zero
        for (i in particle.velocity.particleParams.indices) {
            assertEquals(0.0, particle.velocity.particleParams[i], 0.001)
        }
    }

    @Test
    fun `test Particle initialization with different ranges`() {
        val customRanges = listOf(
            Ranges(-5.0, 5.0),
            Ranges(0.1, 10.0),
            Ranges(-2.0, 2.0),
            Ranges(0.0, 1.0)
        )
        
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(customRanges, fitnessFunction)

        assertNotNull(particle)
        assertEquals(4, particle.position.particleParams.size)
        assertEquals(4, particle.velocity.particleParams.size)
        
        // Check that position is within ranges
        for (i in customRanges.indices) {
            assertTrue("Position should be within range ${i}", 
                particle.position.particleParams[i] >= customRanges[i].start)
            assertTrue("Position should be within range ${i}", 
                particle.position.particleParams[i] <= customRanges[i].stop)
        }
    }

    @Test
    fun `test Particle best result initialization`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        assertEquals(Double.MAX_VALUE, particle.bestResult, 0.001)
    }

    @Test
    fun `test Particle pBestParam initialization`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        // pBestParam should be initialized to the same position
        for (i in particle.position.particleParams.indices) {
            assertEquals(particle.position.particleParams[i], 
                particle.pBestParam.particleParams[i], 0.001)
        }
    }

    @Test
    fun `test Particle updateFitness`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        particle.updateFitness()

        // After updateFitness, bestResult should be updated (not MAX_VALUE)
        assertTrue("Best result should be updated", particle.bestResult < Double.MAX_VALUE)
        assertTrue("Best result should be non-negative", particle.bestResult >= 0.0)
    }

    @Test
    fun `test Particle updateFitness improves best result`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        // First fitness update
        particle.updateFitness()
        val firstBestResult = particle.bestResult

        // Modify position to potentially get better fitness
        particle.position.particleParams[0] = 0.1 // Small kp value
        particle.position.particleParams[1] = 0.1 // Small ki value
        particle.position.particleParams[2] = 0.1 // Small kd value

        // Second fitness update
        particle.updateFitness()
        val secondBestResult = particle.bestResult

        // The best result should be the minimum of both
        assertEquals(minOf(firstBestResult, secondBestResult), particle.bestResult, 0.001)
    }

    @Test
    fun `test Particle updateVelocity`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        val globalBest = Particle(testRanges, fitnessFunction)

        // Set some initial values
        particle.position.particleParams[0] = 1.0
        particle.position.particleParams[1] = 0.5
        particle.position.particleParams[2] = 0.1

        globalBest.position.particleParams[0] = 2.0
        globalBest.position.particleParams[1] = 1.0
        globalBest.position.particleParams[2] = 0.2

        val originalPosition = particle.position.particleParams.clone()

        particle.updateVelocity(globalBest)

        // Position should be updated after velocity update
        for (i in particle.position.particleParams.indices) {
            assertTrue("Position should be updated", 
                particle.position.particleParams[i] != originalPosition[i])
        }
    }

    @Test
    fun `test Particle updateVelocity with negative positions`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        val globalBest = Particle(testRanges, fitnessFunction)

        // Set negative positions to test ensureNonNegativePosition
        particle.position.particleParams[0] = -1.0
        particle.position.particleParams[1] = -0.5
        particle.position.particleParams[2] = -0.1

        globalBest.position.particleParams[0] = 2.0
        globalBest.position.particleParams[1] = 1.0
        globalBest.position.particleParams[2] = 0.2

        particle.updateVelocity(globalBest)

        // After updateVelocity, positions should be non-negative
        // Note: The ensureNonNegativePosition logic only fixes negative values in the velocity vector,
        // not necessarily in the final position after adding velocity to position
        for (i in particle.position.particleParams.indices) {
            assertTrue("Position should be non-negative after update", 
                particle.position.particleParams[i] >= 0.0)
        }
    }

    @Test
    fun `test Particle toString method`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        val result = particle.toString()

        assertNotNull(result)
        assertTrue("String should contain position values", 
            result.contains(particle.position.particleParams[0].toString()))
    }

    @Test
    fun `test Particle printStory method`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        
        // Initialize fitness first by calling updateFitness
        particle.updateFitness()
        
        // This should not throw an exception
        particle.printStory(10)
    }

    @Test
    fun `test Particle with motor simulation`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        particle.updateFitness()

        assertTrue("Best result should be updated", particle.bestResult < Double.MAX_VALUE)
        assertTrue("Best result should be non-negative", particle.bestResult >= 0.0)
    }

    // Performance Tests
    @Test
    fun `test Particle creation performance`() {
        val fitnessFunction = createFitnessFunction()
        
        val creationTime = measureNanoTime {
            repeat(1000) {
                val particle = Particle(testRanges, fitnessFunction)
                assertNotNull(particle)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("Particle creation time for 1000 instances: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("Particle creation took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 100_000)
    }

    @Test
    fun `test Particle updateFitness performance`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)

        val fitnessTime = measureNanoTime {
            particle.updateFitness()
        }

        println("Particle updateFitness time: ${fitnessTime} ns")
        assertTrue("Particle updateFitness took too long: ${fitnessTime} ns", fitnessTime < 1_000_000)
    }

    @Test
    fun `test Particle updateVelocity performance`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        val globalBest = Particle(testRanges, fitnessFunction)

        val velocityTime = measureNanoTime {
            particle.updateVelocity(globalBest)
        }

        println("Particle updateVelocity time: ${velocityTime} ns")
        assertTrue("Particle updateVelocity took too long: ${velocityTime} ns", velocityTime < 100_000)
    }

    @Test
    fun `test Particle batch operations performance`() {
        val fitnessFunction = createFitnessFunction()
        val particles = Array(100) { Particle(testRanges, fitnessFunction) }
        val globalBest = Particle(testRanges, fitnessFunction)

        val batchTime = measureTimeMillis {
            for (particle in particles) {
                particle.updateVelocity(globalBest)
                particle.updateFitness()
            }
        }

        val avgTimePerParticle = (batchTime * 1_000_000) / 200 // 100 velocity updates + 100 fitness updates
        println("Batch particle operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerParticle} ns")

        assertTrue("Average particle operation time too high: ${avgTimePerParticle} ns", 
            avgTimePerParticle < 5_000_000)
    }

    @Test
    fun `test Particle memory efficiency`() {
        val fitnessFunction = createFitnessFunction()
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val particles = Array(1000) { Particle(testRanges, fitnessFunction) }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 1000 particles: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 10 * 1024 * 1024) // Less than 10MB
    }

    @Test
    fun `test Particle operations consistency`() {
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(testRanges, fitnessFunction)
        val globalBest = Particle(testRanges, fitnessFunction)
        val times = mutableListOf<Long>()

        repeat(100) {
            val time = measureNanoTime {
                particle.updateVelocity(globalBest)
                particle.updateFitness()
            }
            times.add(time)
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("Particle operations consistency over 100 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")

        assertTrue("Max particle operation time too high: ${maxTime} ns", maxTime < 10_000_000)
        assertTrue("Particle operation timing variance too high", (maxTime - minTime) < 5_000_000)
    }

    @Test
    fun `test Particle with large ranges`() {
        val largeRanges = listOf(
            Ranges(0.0, 100.0),
            Ranges(0.0, 50.0),
            Ranges(0.0, 25.0),
            Ranges(0.0, 10.0),
            Ranges(0.0, 5.0)
        )
        
        val fitnessFunction = createFitnessFunction()
        val particle = Particle(largeRanges, fitnessFunction)

        assertNotNull(particle)
        assertEquals(5, particle.position.particleParams.size)
        
        // Check that position is within ranges
        for (i in largeRanges.indices) {
            assertTrue("Position should be within large range ${i}", 
                particle.position.particleParams[i] >= largeRanges[i].start)
            assertTrue("Position should be within large range ${i}", 
                particle.position.particleParams[i] <= largeRanges[i].stop)
        }
    }
} 