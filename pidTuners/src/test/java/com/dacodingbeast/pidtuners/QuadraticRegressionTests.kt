package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.Algorithm.QuadraticRegression
import com.dacodingbeast.pidtuners.Algorithm.QuadraticRegression.Companion.quadraticRegressionManual
import junit.framework.TestCase.assertEquals
import org.junit.Test

class QuadraticRegressionTests {
    @Test
    fun testQuadraticRegressionWithDynamicInput() {
        // Simulated data collection loop
        val dataPairs = mutableListOf<Pair<Double, Double>>()

        // Generate test data based on y = 2 + 3x + 5x^2
        for (i in 1..10) {
            val angle = i.toDouble()
            val motorValue = 2 + 3 * angle + 5 * angle * angle
            dataPairs.add(Pair(angle, motorValue))
        }

        // Extract x and y values from dataPairs
        val x = dataPairs.map { it.first }.toDoubleArray()
        val y = dataPairs.map { it.second }.toDoubleArray()

        // Perform quadratic regression
        val coefficients = quadraticRegressionManual(x, y)

        // Display the coefficients
        println("Test Case: Quadratic Regression with Dynamic Input")
        println("Expected coefficients: Intercept = 2, Linear = 3, Quadratic = 5")
        println("Computed coefficients:")
        println("Intercept (a): ${coefficients[0]}")
        println("Linear term (b): ${coefficients[1]}")
        println("Quadratic term (c): ${coefficients[2]}")

        // Validate results with assertions
        assert(kotlin.math.abs(coefficients[0] - 2.0) < 1e-6) { "Intercept is incorrect" }
        assert(kotlin.math.abs(coefficients[1] - 3.0) < 1e-6) { "Linear term is incorrect" }
        assert(kotlin.math.abs(coefficients[2] - 5.0) < 1e-6) { "Quadratic term is incorrect" }

        println("Test passed!")
    }
    @Test
    fun testQuadraticRegression() {
        val points = listOf(
            Pair(1.0, 6.0),
            Pair(2.0, 11.0),
            Pair(3.0, 18.0),
            Pair(4.0, 27.0),
            Pair(5.0, 38.0)
        )
        val x = points.map { it.first }.toDoubleArray()
        val y = points.map { it.second }.toDoubleArray()

        val coefficients = QuadraticRegression.quadraticRegressionManual(x,y)
        val intercept = coefficients[0]
        val linear = coefficients[1]
        val quadratic = coefficients[2]

        assertEquals(3.0, intercept, 0.01) // Expected a = 1.0
        assertEquals(2.0, linear, 0.01) // Expected b = 2.0
        assertEquals(1.0, quadratic, 0.01) // Expected c = 3.0
        println("Computed coefficients: Intercept=$intercept, Linear=$linear, Quadratic=$quadratic")
    }
}