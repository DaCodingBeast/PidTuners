package com.example.pso4pid

import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.math.PI
import kotlin.random.Random

class AngleRangeTests {

    /**
     *  Creating Angle Ranges outside of -PI to PI
     *  Expected Outcome: Illegal Argument Exception
     */
    @Test
    fun createAngleRange_outsideRange() {
        for (i in 0..100) {
            val errorOccurred =
                try {
                    AngleRange(
                        Random.nextDouble(PI, Double.MAX_VALUE), Random.nextDouble(
                            Double.MIN_VALUE, -PI
                        )
                    )
                    false
                } catch (e: IllegalArgumentException) {
                    true
                }
            assert(errorOccurred)
        }
    }

    /**
     *  Testing the Normalizing of Angles for angles outside of -PI to PI
     *  Expected Outcome: Angles in between 0 to 2pi
     */
    @Test
    fun normalizeRadians_outsideRange() {
        for (i in 0..100) {
            val angle = Random.nextDouble(-PI, PI)
            val normalizedAngle = AngleRange.normalizeAngle(angle)
            val expectedOutcome = if(angle <0) angle + 2* PI else angle
            assertEquals(normalizedAngle,expectedOutcome)
        }
    }

    /**
     *  Testing the Normalizing of Angles in between -PI to PI
     *  Positive angles in between 0 to 2pi
     */
    @Test
    fun normalizeRadians() {
        for (i in 0..100) {
            val angle = Random.nextDouble(-PI, PI)
            val normalizedAngle = AngleRange.normalizeAngle(angle)
            val expectedOutcome = if(angle <0) angle + 2* PI else angle
            assertEquals(normalizedAngle,expectedOutcome)
        }
    }

    /**
     *  Test to see if Two Angle Ranges in different Regions of A circle Interfere
     *  Expected Outcome: No interference, test will pass
     */

    @Test
    fun inRange_NonInterferenceCase() {

        //must pass - testing ranges that will not intersect
        for (i in 0..100) {
            val (a, b) = List(2) { Random.nextDouble(0.0, PI) }
            val (c, d) = List(2) { Random.nextDouble(-PI, 0.0) }
            assert(!AngleRange.inRange(AngleRange(a, b), AngleRange(c, d))) { println("$a $b $c $d") }
        }

    }

    /**
     *  Test to see if Two Angle Ranges with the same angles are considered interfering
     *  Expected Outcome: Interference detected
     */

    @Test
    fun inRange_InterferenceCase() {

        assert(AngleRange.inRange(AngleRange(0.0, PI), AngleRange(0.0, 1.0)))
        assert(AngleRange.inRange(AngleRange(-0.0, -1.0), AngleRange(0.0, PI)))
        assert(AngleRange.inRange(AngleRange(-0.0, -3.0), AngleRange(-3.0, PI)))

    }
}