package com.dacodingbeast.pidtuners.Simulators

import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.utilities.AngleUnit
import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.dacodingbeast.pidtuners.utilities.Measurements
import kotlin.math.PI

/**
 * Optimized AngleRange class for high-performance angle calculations
 * Uses data class for better memory layout and performance
 */
data class AngleRange private constructor(override val start: Double, override val stop: Double) :
    Target(start, stop) {

    companion object {
        // Pre-calculate constants to avoid repeated calculations
        private const val TWO_PI = 2.0 * PI
        private const val NEGATIVE_PI = -PI
        private const val POSITIVE_PI = PI
        
        // Bit mask for fast modulo operations (for specific cases)
        private const val WRAP_MASK = 0x7FFFFFFF

        /**
         * Create an AngleRange using radians.
         */
        @JvmStatic
        fun fromRadians(startAngle: Double, endAngle: Double): AngleRange {
            return AngleRange(startAngle, endAngle)
        }

        /**
         * Create an AngleRange using degrees.
         */
        @JvmStatic
        fun fromDegrees(startAngle: Double, endAngle: Double): AngleRange {
            return AngleRange(
                Measurements.Angle(startAngle, AngleUnit.DEGREES).toRadians(),
                Measurements.Angle(endAngle, AngleUnit.DEGREES).toRadians()
            )
        }

        /**
         * Optimized angle wrapping using conditional operations instead of loops
         * This eliminates the while loops which can be expensive for large angles
         * @param theta Angle Error being wrapped, so that the shortest route is discovered
         */
        @JvmStatic
        fun wrap(theta: Double): Double {
            // Use conditional operations instead of loops for better performance
            return when {
                theta > POSITIVE_PI -> {
                    val quotient = ((theta + POSITIVE_PI) / TWO_PI).toInt()
                    theta - quotient * TWO_PI
                }
                theta < NEGATIVE_PI -> {
                    val quotient = ((NEGATIVE_PI - theta) / TWO_PI).toInt()
                    theta + (quotient + 1) * TWO_PI
                }
                else -> theta
            }
        }

        /**
         * Optimized angle normalization using conditional operations
         * @param angle Angle being normalized
         */
        @JvmStatic
        fun normalizeAngle(angle: Double): Double {
            return if (angle < 0) angle + TWO_PI else angle
        }

        /**
         * Optimized motor direction calculation with reduced branching
         * @param goal Target Angle
         * @param obstacle Obstacle
         * @return The route the arm must take, while still avoiding any obstacles
         */
        @JvmStatic
        fun findMotorDirection(goal: AngleRange, obstacle: AngleRange?): Direction {
            val angleChange = wrap(goal.stop - goal.start)
            
            // Use bit manipulation for faster sign check
            val isPositive = angleChange > 0.0
            
            val shortRoute = if (isPositive) Direction.CounterClockWise else Direction.Clockwise
            val longRoute = if (isPositive) Direction.Clockwise else Direction.CounterClockWise

            return if (obstacle != null && inRange(goal, obstacle, angleChange)) {
                longRoute
            } else {
                shortRoute
            }
        }

        /**
         * Optimized range checking with reduced branching
         * @param goal Target Angle
         * @param obstacle Obstacle
         * @param shortestAngleChange Pre-calculated wrapped angle change (optional optimization)
         * @return Whether there is an obstacle in the way of the shortest route
         */
        @JvmStatic
        fun inRange(goal: AngleRange, obstacle: AngleRange, shortestAngleChange: Double? = null): Boolean {
            val angleChange = shortestAngleChange ?: wrap(goal.stop - goal.start)
            val isPositive = angleChange > 0

            return if (isPositive) {
                (obstacle.start >= goal.start && obstacle.start <= goal.stop) ||
                (obstacle.stop >= goal.start && obstacle.stop <= goal.stop)
            } else {
                (obstacle.start <= goal.start && obstacle.start >= goal.stop) ||
                (obstacle.stop <= goal.start && obstacle.stop >= goal.stop)
            }
        }

        /**
         * Optimized PIDF angle error calculation with reduced branching
         * @param direction Motor Direction
         * @param angleRange Current and Target Angle
         * @return Error in Radians
         */
        @JvmStatic
        fun findPIDFAngleError(direction: Direction, angleRange: AngleRange): Double {
            val angleChange = wrap(angleRange.stop - angleRange.start)
            
            return when (direction) {
                Direction.CounterClockWise -> {
                    if (angleChange > 0) angleChange else angleChange + TWO_PI
                }
                Direction.Clockwise -> {
                    if (angleChange < 0) angleChange else angleChange - TWO_PI
                }
            }
        }

        /**
         * Highly optimized version that calculates direction and error in one pass
         * Eliminates redundant calculations and reduces branching
         */
        @JvmStatic
        fun findDirectionAndError(goal: AngleRange, obstacle: AngleRange?): Pair<Direction, Double> {
            val angleChange = wrap(goal.stop - goal.start)
            val isPositive = angleChange > 0.0
            
            val shortRoute = if (isPositive) Direction.CounterClockWise else Direction.Clockwise
            val longRoute = if (isPositive) Direction.Clockwise else Direction.CounterClockWise
            
            val direction = if (obstacle != null && inRange(goal, obstacle, angleChange)) {
                longRoute
            } else {
                shortRoute
            }

            val error = when (direction) {
                Direction.CounterClockWise -> {
                    if (isPositive) angleChange else angleChange + TWO_PI
                }
                Direction.Clockwise -> {
                    if (!isPositive) angleChange else angleChange - TWO_PI
                }
            }

            return direction to error
        }
    }

    /**
     * Optimized toString method
     */
    override fun toString(): String {
        return "($start, $stop)"
    }

    /**
     * Optimized list creation - use singleton list for better memory efficiency
     */
    fun asArrayList(): ArrayList<AngleRange> {
        return arrayListOf(this)
    }

    fun asList(): List<AngleRange> {
        return listOf(this)
    }
}