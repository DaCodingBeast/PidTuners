import com.dacodingbeast.pidtuners.Algorithm.Vector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class VectorTests {

    @Test
    fun `test Vector initialization with positive values`() {
        val values = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        val vector = Vector(values)

        assertNotNull(vector)
        assertEquals(4, vector.particleParams.size)
        assertEquals(1.0, vector.particleParams[0], 0.001)
        assertEquals(2.0, vector.particleParams[1], 0.001)
        assertEquals(3.0, vector.particleParams[2], 0.001)
        assertEquals(4.0, vector.particleParams[3], 0.001)
    }

    @Test
    fun `test Vector initialization with negative values`() {
        val values = doubleArrayOf(-1.0, -2.0, -3.0)
        val vector = Vector(values)

        assertNotNull(vector)
        assertEquals(3, vector.particleParams.size)
        assertEquals(-1.0, vector.particleParams[0], 0.001)
        assertEquals(-2.0, vector.particleParams[1], 0.001)
        assertEquals(-3.0, vector.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector initialization with zero values`() {
        val values = doubleArrayOf(0.0, 0.0, 0.0)
        val vector = Vector(values)

        assertNotNull(vector)
        assertEquals(3, vector.particleParams.size)
        assertEquals(0.0, vector.particleParams[0], 0.001)
        assertEquals(0.0, vector.particleParams[1], 0.001)
        assertEquals(0.0, vector.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector addition with positive vectors`() {
        val vector1 = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val vector2 = Vector(doubleArrayOf(4.0, 5.0, 6.0))

        val result = vector1 + vector2

        assertNotNull(result)
        assertEquals(5.0, result.particleParams[0], 0.001)
        assertEquals(7.0, result.particleParams[1], 0.001)
        assertEquals(9.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector addition with negative vectors`() {
        val vector1 = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val vector2 = Vector(doubleArrayOf(-1.0, -2.0, -3.0))

        val result = vector1 + vector2

        assertNotNull(result)
        assertEquals(0.0, result.particleParams[0], 0.001)
        assertEquals(0.0, result.particleParams[1], 0.001)
        assertEquals(0.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector subtraction with positive vectors`() {
        val vector1 = Vector(doubleArrayOf(5.0, 7.0, 9.0))
        val vector2 = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        val result = vector1 - vector2

        assertNotNull(result)
        assertEquals(4.0, result.particleParams[0], 0.001)
        assertEquals(5.0, result.particleParams[1], 0.001)
        assertEquals(6.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector subtraction with negative result`() {
        val vector1 = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val vector2 = Vector(doubleArrayOf(5.0, 7.0, 9.0))

        val result = vector1 - vector2

        assertNotNull(result)
        assertEquals(-4.0, result.particleParams[0], 0.001)
        assertEquals(-5.0, result.particleParams[1], 0.001)
        assertEquals(-6.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector scalar multiplication with positive scalar`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val scalar = 2.5

        val result = vector * scalar

        assertNotNull(result)
        assertEquals(2.5, result.particleParams[0], 0.001)
        assertEquals(5.0, result.particleParams[1], 0.001)
        assertEquals(7.5, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector scalar multiplication with negative scalar`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val scalar = -2.0

        val result = vector * scalar

        assertNotNull(result)
        assertEquals(-2.0, result.particleParams[0], 0.001)
        assertEquals(-4.0, result.particleParams[1], 0.001)
        assertEquals(-6.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector scalar multiplication with zero`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val scalar = 0.0

        val result = vector * scalar

        assertNotNull(result)
        assertEquals(0.0, result.particleParams[0], 0.001)
        assertEquals(0.0, result.particleParams[1], 0.001)
        assertEquals(0.0, result.particleParams[2], 0.001)
    }

    @Test
    fun `test Vector toString method`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val result = vector.toString()

        assertNotNull(result)
        assertTrue("String should contain vector values", result.contains("1.0") && result.contains("2.0") && result.contains("3.0"))
    }

    @Test
    fun `test Vector toString with even number of elements`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0))
        val result = vector.toString()

        assertNotNull(result)
        assertTrue("String should end with 0.0 for even elements", result.endsWith("0.0"))
    }

    @Test
    fun `test ensureNonNegativePosition with negative values`() {
        val swarmBest = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val particlePosition = Vector(doubleArrayOf(-1.0, -2.0, -3.0))
        val vector = Vector(doubleArrayOf(-0.5, -1.5, -2.5))

        vector.ensureNonNegativePosition(swarmBest, particlePosition)

        assertEquals(1.0, vector.particleParams[0], 0.001)
        assertEquals(2.0, vector.particleParams[1], 0.001)
        assertEquals(3.0, vector.particleParams[2], 0.001)
    }

    @Test
    fun `test ensureNonNegativePosition with mixed values`() {
        val swarmBest = Vector(doubleArrayOf(-1.0, 2.0, -3.0))
        val particlePosition = Vector(doubleArrayOf(0.5, -1.5, 2.5))
        val vector = Vector(doubleArrayOf(-0.5, -1.5, -2.5))

        vector.ensureNonNegativePosition(swarmBest, particlePosition)

        assertEquals(0.5, vector.particleParams[0], 0.001)
        assertEquals(2.0, vector.particleParams[1], 0.001)
        assertEquals(2.5, vector.particleParams[2], 0.001)
    }

    // Performance Tests
    @Test
    fun `test Vector creation performance`() {
        val values = DoubleArray(100) { it.toDouble() }

        val creationTime = measureNanoTime {
            val vector = Vector(values)
            assertNotNull(vector)
        }

        println("Vector creation time: ${creationTime} ns")
        assertTrue("Vector creation took too long: ${creationTime} ns", creationTime < 10_000)
    }

    @Test
    fun `test Vector addition performance`() {
        val vector1 = Vector(DoubleArray(100) { it.toDouble() })
        val vector2 = Vector(DoubleArray(100) { it.toDouble() * 2 })

        val additionTime = measureNanoTime {
            val result = vector1 + vector2
            assertNotNull(result)
        }

        println("Vector addition time: ${additionTime} ns")
        assertTrue("Vector addition took too long: ${additionTime} ns", additionTime < 50_000)
    }

    @Test
    fun `test Vector subtraction performance`() {
        val vector1 = Vector(DoubleArray(100) { it.toDouble() })
        val vector2 = Vector(DoubleArray(100) { it.toDouble() * 2 })

        val subtractionTime = measureNanoTime {
            val result = vector1 - vector2
            assertNotNull(result)
        }

        println("Vector subtraction time: ${subtractionTime} ns")
        assertTrue("Vector subtraction took too long: ${subtractionTime} ns", subtractionTime < 50_000)
    }

    @Test
    fun `test Vector scalar multiplication performance`() {
        val vector = Vector(DoubleArray(100) { it.toDouble() })
        val scalar = 2.5

        val multiplicationTime = measureNanoTime {
            val result = vector * scalar
            assertNotNull(result)
        }

        println("Vector scalar multiplication time: ${multiplicationTime} ns")
        assertTrue("Vector scalar multiplication took too long: ${multiplicationTime} ns", multiplicationTime < 50_000)
    }

    @Test
    fun `test Vector operations batch performance`() {
        val vectors = Array(100) { Vector(DoubleArray(50) { it.toDouble() }) }
        val scalar = 1.5

        val batchTime = measureTimeMillis {
            for (i in 0 until 99) {
                val result = vectors[i] + vectors[i + 1]
                val multiplied = result * scalar
                assertNotNull(multiplied)
            }
        }

        val avgTimePerOperation = (batchTime * 1_000_000) / 198 // 99 additions + 99 multiplications
        println("Batch vector operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerOperation} ns")

        assertTrue("Average vector operation time too high: ${avgTimePerOperation} ns", avgTimePerOperation < 100_000)
    }

    @Test
    fun `test ensureNonNegativePosition performance`() {
        val swarmBest = Vector(DoubleArray(100) { it.toDouble() })
        val particlePosition = Vector(DoubleArray(100) { -it.toDouble() })
        val vector = Vector(DoubleArray(100) { -it.toDouble() })

        val ensureTime = measureNanoTime {
            vector.ensureNonNegativePosition(swarmBest, particlePosition)
        }

        println("ensureNonNegativePosition time: ${ensureTime} ns")
        assertTrue("ensureNonNegativePosition took too long: ${ensureTime} ns", ensureTime < 100_000)
    }

    @Test
    fun `test Vector toString performance`() {
        val vector = Vector(DoubleArray(50) { it.toDouble() })

        val toStringTime = measureNanoTime {
            val result = vector.toString()
            assertNotNull(result)
        }

        println("Vector toString time: ${toStringTime} ns")
        assertTrue("Vector toString took too long: ${toStringTime} ns", toStringTime < 100_000)
    }

    @Test
    fun `test Vector memory efficiency`() {
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val vectors = Array(1000) { Vector(DoubleArray(10) { it.toDouble() }) }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 1000 vectors: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 1024 * 1024) // Less than 1MB
    }
} 