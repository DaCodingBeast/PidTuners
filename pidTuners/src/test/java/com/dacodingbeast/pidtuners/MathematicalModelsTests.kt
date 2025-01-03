//package com.example.pso4pid
//
//import ArmSpecific.pso4Arms
//import CommonUtilities.Models
//import junit.framework.TestCase.assertEquals
//import org.junit.Test
//import kotlin.math.PI
//import kotlin.math.abs
//import kotlin.math.pow
//import kotlin.random.Random
//
//class MathematicalModelsTests {
//
//    init {
//        pso4Arms.System.SystemConstants = Constants.constant
//    }
//
//    /**
//     * Tests gravitational torque to see if the math model is prepared for negative and positive angles, and if gravity torque is always positive
//     * Expected Result: The exact same torque for the positive and negative version of a number
//     * @see Models.gravityTorque
//     */
//    @Test
//    fun gravitationalModelBasedOn_Angle() {
//        for (i in 0..100) {
//            val random = Random.nextDouble(-PI, PI)
//            val a = Models.gravityTorque(random)
//            val b = Models.gravityTorque(-random)
//            assertEquals(a, b)
//            assert(a >= 0 && b>=0)
//        }
//    }
//
//
//    /**
//     * Pass a range of inputs to Gravity Torque Model
//     * Expected outcome:  Return values match the expected values.
//     * @see Models.gravityTorque
//     */
//
//    @Test
//    fun gravityModel_setInputAndOutput() {
//        val inputs = List(100){Random.nextDouble(-PI,PI)}
//
//        for (input in inputs){
//            val constants = pso4Arms.System.SystemConstants.gravityConstants
//            val expectedResult = constants.a * (abs(input) - constants.b).pow(2) + constants.k
//            val actualResult = Models.gravityTorque(input)
//            assertEquals(expectedResult, actualResult)
//        }
//    }
//
//
//    /**
//     * Test to make sure the function has a restricted domain
//     * @see Models.gravityTorque
//     */
//    @Test
//    fun mathModelRestrictedDomain() {
//        for (i in 0..100){
//            val errorDetected = try {
//                Models.gravityTorque(Random.nextDouble(PI, Double.MAX_VALUE))
//                Models.gravityTorque(Random.nextDouble(Double.MIN_VALUE,-PI))
//                false
//            }catch (e: IllegalArgumentException){
//                true
//            }
//            assert(errorDetected)
//        }
//    }
//
//}