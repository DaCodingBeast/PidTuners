package com.dacodingbeast.pidtuners.Mathematics

import ArmSpecific.Direction
import android.util.Log
import kotlin.math.PI

/**
 * Angle Unit used throughout simulation
 */
data class AngleRange(var start: Double, var target: Double) {

    /**
     * List of Functions preformed on everything involving Angles
     */
    companion object Angles{
        /**
         * Wrapping the Angle in -PI to PI range
         * @param theta Angle Error being wrapped, so that the shortest route is discovered
         */
        fun wrap(theta: Double): Double {
//            require(theta in -2 * PI..2 * PI) { Log.d(ArmSpecific.error, "You created an Angle greater than 360 degrees")}
            var angle = theta
            while (angle > PI) angle -= PI * 2
            while (angle < -PI) angle += PI * 2
            return angle
        }

        /**
         * Normalizing a Wrapped Angle into a 0 to 2PI range
         * @param angle Angle being normalized
         */
        fun normalizeAngle(angle: Double): Double {
            val twoPi = 2 * Math.PI
            return if (angle < 0) angle + twoPi else angle
        }

        /**
         * Finding the Motor Direction while accounting for any [obstacle]
         * @param goal Target Angle
         * @param obstacle Obstacle
         * @return The route the arm must take, while still avoiding any obstacles
         */
        fun findMotorDirection(goal: AngleRange, obstacle: AngleRange?): Direction {
            val (shortRoute, longRoute) = if (wrap(goal.target - goal.start) > 0.0) {
                Direction.CounterClockWise to Direction.Clockwise
            } else {
                Direction.Clockwise to Direction.CounterClockWise
            }
            return if (obstacle != null) {
//            println("  current angles$g1, $g2")
                if (inRange(goal,obstacle)) longRoute else shortRoute
            } else shortRoute
        }
        /**
         * Finding If the shortest route to an Angle could be prevented by an [obstacle]
         * @param goal Target Angle
         * @param obstacle Obstacle
         * @return Whether there is an obstacle in the way of the shortest route
         */
        fun inRange(goal: AngleRange, obstacle: AngleRange): Boolean {

            val shortestAngleChange = wrap(goal.target - goal.start)
            for(o in listOf(obstacle.start, obstacle.target)){
                return if (shortestAngleChange>0){
                    o >= goal.start && o<= goal.target
                } else{
                    o <= goal.start && o>= goal.target
                }
            }
            return false
        }

        /**
         * Find the Error supplied to the PIDF Controller, based on the motor Direction
         * @param direction Motor Direction
         * @param angleRange Current and Target Angle
         * @return Error in Radians
         */
        fun findPIDFAngleError(direction: Direction, angleRange: AngleRange): Double {
            val angleChange = wrap(angleRange.target - angleRange.start)
            return when (direction) {
                Direction.CounterClockWise -> {
                    if (angleChange>0){
                        angleChange
                    } else{
                        angleChange + 2 * PI
                    }
                }

                Direction.Clockwise -> {
                    if (angleChange<0){
                        angleChange
                    } else{
                        angleChange - 2* PI
                    }
                }
            }
        }
    }

    init {

        /**
         * Requirements that the Angle Range must be within -PI to PI
         */
        require(start in -PI..PI) { Log.d(ArmSpecific.error,"Angle Ranges should be in Radians") }
        require(target in -PI..PI) { Log.d(ArmSpecific.error,"Angle Ranges should be in Radians") }

        start = wrap(start)
        target = wrap(target)

    }
    /**
     * To String method to display changes in Arms positions
     */
    override fun toString(): String {
        return "(${this.start}, ${this.target})"
    }

}