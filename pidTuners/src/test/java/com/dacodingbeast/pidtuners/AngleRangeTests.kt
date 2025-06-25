package com.dacodingbeast.pidtuners.Mathematics

import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlin.math.PI

class AngleRangeExtendedTests {
    @Test
    fun testAngleRangeNewConstructs(){
        val range = AngleRange.fromRadians(0.0,PI/2)
        assertEquals(0.0,range.start,1e-9)
        assertEquals(PI/2,range.stop,1e-9)
        val range2 = AngleRange.fromDegrees(0.0,90.0)
        assertEquals(0.0,range2.start,1e-9)
        assertEquals(PI/2,range2.stop,1e-9)

    }

    @Test
    fun testWrapPositiveOverflow() {
        val overflow = 3 * PI
        assertEquals(PI, AngleRange.wrap(overflow))
    }

    @Test
    fun testWrapNegativeOverflow() {
        val underflow = -3 * PI
        assertEquals(-PI, AngleRange.wrap(underflow))
    }

    @Test
    fun testWrapLargePositiveAngle() {
        val bigAngle = 5 * PI / 2  // 450 degrees
        assertEquals(PI / 2, AngleRange.wrap(bigAngle))
    }

    @Test
    fun testWrapLargeNegativeAngle() {
        val bigNegativeAngle = -5 * PI / 2  // -450 degrees
        assertEquals(-PI / 2, AngleRange.wrap(bigNegativeAngle))
    }

    @Test
    fun testNormalizeZero() {
        assertEquals(0.0, AngleRange.normalizeAngle(0.0))
    }

    @Test
    fun testNormalizePositiveAngle() {
        assertEquals(PI / 3, AngleRange.normalizeAngle(PI / 3))
    }

    @Test
    fun testNormalizeNegativeAngleWrapAround() {
        assertEquals(3 * PI / 2, AngleRange.normalizeAngle(-PI / 2))
    }

    @Test
    fun testFromDegrees360() {
        val range = AngleRange.fromRadians(360.0, 0.0)
        assertEquals(360.0, range.start, 1e-9)
        assertEquals(0.0, range.stop, 1e-9)
    }

    @Test
    fun testFromRadiansNegativeFullCircle() {
        val range = AngleRange.fromRadians(-2 * PI, 0.0)
        assertEquals(-2 * PI, range.start, 1e-9)
        assertEquals(0.0, range.stop, 1e-9)
    }

    @Test
    fun testFindMotorDirectionNoObstacleShortRoute() {
        val range = AngleRange.fromRadians(0.0, PI / 2)
        assertEquals(Direction.CounterClockWise, AngleRange.findMotorDirection(range, null))
    }

    @Test
    fun testFindMotorDirectionWithObstacleStillShortRoute() {
        val goal = AngleRange.fromRadians(PI / 2, PI)
        val obstacle = AngleRange.fromRadians(3 * PI / 2, -PI / 2) // Not blocking
        assertEquals(Direction.CounterClockWise, AngleRange.findMotorDirection(goal, obstacle))
    }

    @Test
    fun testFindMotorDirectionObstacleBlockingShortRoute() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3) // Blocking
        assertEquals(Direction.Clockwise, AngleRange.findMotorDirection(goal, obstacle))
    }

    @Test
    fun testInRangePositiveShortRoute() {
        val goal = AngleRange.fromRadians(0.0, PI)
        val obstacle = AngleRange.fromRadians(PI / 2, PI / 2)
        assertTrue(AngleRange.inRange(goal, obstacle))
    }

    @Test
    fun testInRangeNegativeShortRoute() {
        val goal = AngleRange.fromRadians(PI, 0.0)
        val obstacle = AngleRange.fromRadians(PI / 2, PI / 2)
        assertTrue(AngleRange.inRange(goal, obstacle))
    }

    @Test
    fun testFindPIDFAngleErrorClockwiseWrap() {
        val range = AngleRange.fromRadians(PI / 2, -PI / 2)
        val error = AngleRange.findPIDFAngleError(Direction.Clockwise, range)
        assertEquals(-PI, error, 1e-9)
    }

    @Test
    fun testFindPIDFAngleErrorCounterClockwiseWrap() {
        val range = AngleRange.fromRadians(-PI / 2, PI / 2)
        val error = AngleRange.findPIDFAngleError(Direction.CounterClockWise, range)
        assertEquals(PI, error, 1e-9)
    }

    @Test
    fun testAsArrayList() {
        val range = AngleRange.fromRadians(0.0, PI / 2)
        val list = range.asArrayList()
        assertEquals(1, list.size)
        assertEquals(range, list[0])
    }

    @Test
    fun testAsList() {
        val range = AngleRange.fromRadians(PI, -PI)
        val list = range.asList()
        assertEquals(1, list.size)
        assertEquals(range, list[0])
    }
}
