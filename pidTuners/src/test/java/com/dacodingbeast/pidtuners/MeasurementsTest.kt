package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.HardwareSetup.AnalogEncoderCalculator
import com.dacodingbeast.pidtuners.HardwareSetup.Operand
import com.dacodingbeast.pidtuners.HardwareSetup.Operation
import com.dacodingbeast.pidtuners.utilities.AngleUnit
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.dacodingbeast.pidtuners.utilities.Measurements
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test
import kotlin.math.PI

class MeasurementsTest {
    @Test
    fun testAngleUnits(){
        val angle = Measurements.Angle(40.0)
        assertEquals(angle.unit, AngleUnit.DEGREES)
        assertEquals(angle.toDegrees(),40.0)
        assertEquals(angle.toRadians(),0.6981317007977317,0.05)
        assertEquals(angle.toAngleUnit(AngleUnit.DEGREES).number, 40.0)
        assertEquals(angle.toAngleUnit(AngleUnit.RADIANS).number, 0.6981317007977317,0.05)
        assertEquals(Measurements.Angle(400.0).wrap().toDegrees(),40.0,0.05)
    }
    @Test
    fun angle_normalize_positiveAngleUnchanged() {
        val angle = Measurements.Angle(90.0, AngleUnit.DEGREES) // 0.5 * PI radians
        val expected = 0.5 * PI
        assertEquals(expected, angle.normalize(), 0.000001)
    }
    @Test
    fun angle_normalize_zeroAngleUnchanged() {
        val angle = Measurements.Angle(0.0, AngleUnit.DEGREES)
        val expected = 0.0
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun angle_normalize_smallNegativeAngleBecomesPositive() {
        val angle = Measurements.Angle(-90.0, AngleUnit.DEGREES) // -0.5 * PI radians
        val expected = 1.5 * PI // -0.5 * PI + 2 * PI
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun angle_normalize_angleGreaterThanTwoPi_isNotReduced() {
        // This test demonstrates the current behavior for angles >= 2π
        val angle = Measurements.Angle(450.0, AngleUnit.DEGREES) // 2.5 * PI radians
        val expected = 2.5 * PI // Current normalize() doesn't reduce this
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun angle_normalize_largeNegativeAngle_isNotFullyNormalized() {
        // This test demonstrates the current behavior for angles < -2π
        val angle = Measurements.Angle(-450.0, AngleUnit.DEGREES) // -2.5 * PI radians
        val expected = -0.5 * PI // -2.5 * PI + 2 * PI
        // This is still negative, not in [0, 2π)
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun angle_normalize_angleAtNegativeTwoPi() {
        val angle = Measurements.Angle.ofDegrees(-360.0) // -2.0 * PI radians
        val expected = 0.0 // -2.0 * PI + 2 * PI
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun angle_normalize_angleJustBelowZero() {
        val angle = Measurements.Angle(-0.001, AngleUnit.RADIANS)
        val expected = -0.001 + 2 * PI
        Assert.assertEquals(expected, angle.normalize(), 0.000001)
    }

    @Test
    fun distanceUnitTests(){
        val distance = Measurements.Distance(40.0)
        assertEquals(distance.unit, DistanceUnit.INCHES)
        assertEquals(distance.toInches(1.0),40.0)
        assertEquals(distance.toCm(1.0),101.6)
        assertEquals(distance.toTicks(10.0),400.0)
        assertEquals(distance.toDistanceUnit(DistanceUnit.CM,1.0).number, 101.6)
        assertEquals(distance.toDistanceUnit(DistanceUnit.INCHES,1.0).number, 40.0)
        assertEquals(distance.toDistanceUnit(DistanceUnit.TICKS,10.0).number, 400.0)

        val cm = Measurements.Distance(40.0,DistanceUnit.CM)
        assertEquals(cm.unit, DistanceUnit.CM)
        assertEquals(cm.toInches(1.0),15.748,0.05)
        assertEquals(cm.toCm(1.0),40.0)
        assertEquals(cm.toTicks(10.0),15.748*10,0.05)

        val ticks = Measurements.Distance(40.0,DistanceUnit.TICKS)
        assertEquals(ticks.unit, DistanceUnit.TICKS)
        assertEquals(ticks.toInches(1892.0),0.0211416490486,0.05)
        assertEquals(ticks.toCm(1892.0),0.0211416490486*2.54,0.00005)
        assertEquals(ticks.toTicks(1.0),40.0)


    }
}