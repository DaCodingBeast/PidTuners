package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.Algorithm.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

class VectorTest {

    @Test
    fun `test ensureNonNegativePosition with negative value and positive swarmBestPosition`() {
        // Create test vectors
        val particlePosition = Vector(doubleArrayOf(1.0, -2.0, 3.0))
        val swarmBestPosition = Vector(doubleArrayOf(5.0, 10.0, 15.0))

        // Ensure non-negative position should replace -2.0 with 10.0 from swarmBestPosition
        particlePosition.ensureNonNegativePosition(swarmBestPosition, particlePosition)

        // Assert that negative values are replaced with corresponding values from swarmBestPosition
        assertEquals(1.0, particlePosition.particleParams[0])
        assertEquals(10.0, particlePosition.particleParams[1])
        assertEquals(3.0, particlePosition.particleParams[2])
    }

    @Test
    fun `test ensureNonNegativePosition with negative value and negative swarmBestPosition`() {
        // Create test vectors
        val particlePosition = Vector(doubleArrayOf(1.0, -2.0, 3.0))
        val swarmBestPosition = Vector(doubleArrayOf(-5.0, -10.0, -15.0))

        // Ensure non-negative position should replace -2.0 with the absolute value of 3.0 from particlePosition
        particlePosition.ensureNonNegativePosition(swarmBestPosition, particlePosition)

        // Assert that negative values are replaced with absolute values from particlePosition
        assertEquals(1.0, particlePosition.particleParams[0])
        assertEquals(abs(3.0), particlePosition.particleParams[1]) // Absolute value of 3.0
        assertEquals(3.0, particlePosition.particleParams[2])
    }

    @Test
    fun `test plus operator`() {
        val vector1 = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val vector2 = Vector(doubleArrayOf(4.0, 5.0, 6.0))

        // Testing the plus operator (+)
        val result = vector1 + vector2
        assertEquals(doubleArrayOf(5.0, 7.0, 9.0), result.particleParams)
    }

    @Test
    fun `test minus operator`() {
        val vector1 = Vector(doubleArrayOf(10.0, 20.0, 30.0))
        val vector2 = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        // Testing the minus operator (-)
        val result = vector1 - vector2
        assertEquals(doubleArrayOf(9.0, 18.0, 27.0), result.particleParams)
    }

    @Test
    fun `test times operator`() {
        val vector = Vector(doubleArrayOf(2.0, 4.0, 6.0))

        // Testing the times operator (*)
        val result = vector * 2.0
        assertEquals(doubleArrayOf(4.0, 8.0, 12.0), result.particleParams)
    }

    @Test
    fun `test toString method`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        // Testing the toString method
        assertEquals("1.0, 2.0, 3.0, 0.0", vector.toString()) // Ensure default toString format
    }
}
