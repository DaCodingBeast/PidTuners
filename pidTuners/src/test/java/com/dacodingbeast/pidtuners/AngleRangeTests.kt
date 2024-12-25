package com.dacodingbeast.pidtuners.Mathematics

import ArmSpecific.Direction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlin.math.PI

class AngleRangeTests {

    /**
     * Test the creation of an AngleRange using radians.
     */
    @Test
    fun testFromRadians() {
        val range = AngleRange.fromRadians(PI / 2, -PI / 2)
        assertEquals(PI / 2, range.start)
        assertEquals(-PI / 2, range.target)
    }

    /**
     * Test the creation of an AngleRange using degrees.
     */
    @Test
    fun testFromDegrees() {
        val range = AngleRange.fromDegrees(90.0, -90.0)
        assertEquals(PI / 2, range.start)
        assertEquals(-PI / 2, range.target)
    }

    /**
     * Test the wrap function to keep angles within the -PI to PI range.
     */
    @Test
    fun testWrap() {
        assertEquals(PI / 2, AngleRange.wrap(PI / 2))
        assertEquals(-PI / 2, AngleRange.wrap(-PI / 2))
        assertEquals(-PI, AngleRange.wrap(-PI))
        assertEquals(PI, AngleRange.wrap(PI))
        assertEquals(0.0, AngleRange.wrap(2 * PI))
        assertEquals(-PI / 2, AngleRange.wrap(-3 * PI / 2))
    }

    /**
     * Test normalizing angles into the 0 to 2PI range.
     */
    @Test
    fun testNormalizeAngle() {
        assertEquals(PI / 2, AngleRange.normalizeAngle(PI / 2))
        assertEquals(3 * PI / 2, AngleRange.normalizeAngle(-PI / 2))
        assertEquals(2 * PI, AngleRange.normalizeAngle(0.0))
        assertEquals(PI, AngleRange.normalizeAngle(-PI))
    }

    /**
     * Test the findMotorDirection function with and without obstacles.
     */
    @Test
    fun testFindMotorDirection() {
        val goal = AngleRange.fromRadians(PI / 4, 3 * PI / 4)

        // No obstacle, should choose short route
        assertEquals(Direction.CounterClockWise, AngleRange.findMotorDirection(goal, null))

        val obstacle = AngleRange.fromRadians(PI / 2, PI)
        // With obstacle, should choose long route
        assertEquals(Direction.Clockwise, AngleRange.findMotorDirection(goal, obstacle))
    }

    /**
     * Test if the inRange function detects interference.
     */
    @Test
    fun testInRange() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle1 = AngleRange.fromRadians(PI / 4, PI / 3)
        val obstacle2 = AngleRange.fromRadians(PI, -PI / 2)

        assertTrue(AngleRange.inRange(goal, obstacle1)) // Overlapping range
        assertFalse(AngleRange.inRange(goal, obstacle2)) // Non-overlapping range
    }

    /**
     * Test the findPIDFAngleError function for both directions.
     */
    @Test
    fun testFindPIDFAngleError() {
        val range = AngleRange.fromRadians(0.0, PI / 2)

        // CounterClockWise direction
        assertEquals(PI / 2, AngleRange.findPIDFAngleError(Direction.CounterClockWise, range))

        // Clockwise direction
        assertEquals(-3 * PI / 2, AngleRange.findPIDFAngleError(Direction.Clockwise, range))
    }

    /**
     * Test the toDegrees function for angle conversion.
     */
    @Test
    fun testToDegrees() {
        val range = AngleRange.fromRadians(PI / 2, -PI / 2)
        val degrees = range.toDegrees()
        assertEquals(90.0, degrees.first)
        assertEquals(-90.0, degrees.second)
    }

    /**
     * Test the toString function for proper formatting.
     */
    @Test
    fun testToString() {
        val range = AngleRange.fromRadians(PI / 4, -PI / 4)
        assertEquals("(0.7853981633974483, -0.7853981633974483)", range.toString())
    }
}
