package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.Algorithm.Vector
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
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
        assertEquals(abs(2.0), particlePosition.particleParams[1]) // Absolute value of 3.0
        assertEquals(3.0, particlePosition.particleParams[2])
    }

    @Test
    fun `test toString method`() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        // Testing the toString method
        assertEquals("1.0, 2.0, 3.0, ", vector.toString()) // Ensure default toString format
    }

    @Test
    fun testEnsureNonNegativePosition() {
        val swarmBestPosition = Vector(doubleArrayOf(1.0, -2.0, 3.0))
        val particlePosition = Vector(doubleArrayOf(-1.0, 2.0, -3.0))
        val vector = Vector(doubleArrayOf(-1.0, -2.0, 3.0))

        vector.ensureNonNegativePosition(swarmBestPosition, particlePosition)

        assertArrayEquals(doubleArrayOf(1.0, 2.0, 3.0), vector.particleParams, 0.0)
    }

    @Test
    fun testPlusOperator() {
        val vector1 = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val vector2 = Vector(doubleArrayOf(4.0, 5.0, 6.0))

        val result = vector1 + vector2

        assertArrayEquals(doubleArrayOf(5.0, 7.0, 9.0), result.particleParams, 0.0)
    }

    @Test
    fun testMinusOperator() {
        val vector1 = Vector(doubleArrayOf(4.0, 5.0, 6.0))
        val vector2 = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        val result = vector1 - vector2

        assertArrayEquals(doubleArrayOf(3.0, 3.0, 3.0), result.particleParams, 0.0)
    }

    @Test
    fun testTimesOperator() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val scalar = 2.0

        val result = vector * scalar

        assertArrayEquals(doubleArrayOf(2.0, 4.0, 6.0), result.particleParams, 0.0)
    }

    @Test
    fun testToString() {
        val vector = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        val result = vector.toString()

        assertEquals("1.0, 2.0, 3.0, ", result)
    }
}
