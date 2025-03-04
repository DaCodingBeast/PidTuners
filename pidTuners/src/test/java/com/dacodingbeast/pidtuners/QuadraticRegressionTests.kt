package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.MathFunctions.QuadraticRegression
import com.dacodingbeast.pidtuners.MathFunctions.QuadraticRegression.Companion.quadraticRegressionManual
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
    @Test
    fun testToVertexForm() {
        // Test case inputs
        val a = 1.0
        val b = -4.0
        val c = 3.0

        // Expected outputs
        val expectedA = 1.0
        val expectedH = 2.0 // h = -b / (2 * a)
        val expectedK = -1.0 // k = c - (b^2 / 4 * a)

        // Run the function
        val result = QuadraticRegression.toVertexForm(a, b, c)

        // Assert results
        assert(result[0] == expectedA) { "Expected a = $expectedA but got ${result[0]}" }
        assert(result[1] == expectedH) { "Expected h = $expectedH but got ${result[1]}" }
        assert(result[2] == expectedK) { "Expected k = $expectedK but got ${result[2]}" }

        // Print success if no assertion fails
        println("Test passed: Vertex form coefficients are correct.")
    }
    @Test
fun testQuadraticRegressionAndVertexForm() {
    // Test data points
    val points = listOf(
        Pair(1.0, 6.0),
        Pair(2.0, 11.0),
        Pair(3.0, 18.0),
        Pair(4.0, 27.0),
        Pair(5.0, 38.0)
    )
    val x = points.map { it.first }.toDoubleArray()
    val y = points.map { it.second }.toDoubleArray()

    // Perform quadratic regression
    val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)
    val intercept = coefficients[0]
    val linear = coefficients[1]
    val quadratic = coefficients[2]

    // Assert the regression coefficients
    assertEquals(3.0, intercept, 0.01) // Expected a = 3.0
    assertEquals(2.0, linear, 0.01) // Expected b = 2.0
    assertEquals(1.0, quadratic, 0.01) // Expected c = 1.0
    println("Computed coefficients: Intercept=$intercept, Linear=$linear, Quadratic=$quadratic")

    // Convert to vertex form
    val vertexForm = QuadraticRegression.toVertexForm(quadratic, linear, intercept)
    val expectedA = 1.0
    val expectedH = -1.0 // h = -b / (2 * a)
    val expectedK = 2.0 // k = c - (b^2 / 4 * a)

    // Assert the vertex form coefficients
    assertEquals(expectedA, vertexForm[0], 0.01)
    assertEquals(expectedH, vertexForm[1], 0.01)
    assertEquals(expectedK, vertexForm[2], 0.01)
    println("Vertex form coefficients: a=${vertexForm[0]}, h=${vertexForm[1]}, k=${vertexForm[2]}")
}

    @Test
    fun testQuadraticRegressionManual() {
        // Test dataset
        val xValues = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val yValues = doubleArrayOf(1.0, 4.0, 9.0, 16.0, 25.0) // y = x^2 (perfect quadratic)

        val coefficients = QuadraticRegression.quadraticRegressionManual(xValues,yValues)
        val intercept = coefficients[0]
        val linear = coefficients[1]
        val quadratic = coefficients[2]

        val expectedA = 1.0 // Coefficient of x^2
        val expectedB = 0.0 // Coefficient of x
        val expectedC = 0.0 // Constant term

        // Assertions to check correctness
        assertEquals(quadratic,expectedA, 1e-6)
        assertEquals(linear,expectedB, 1e-6)
        assertEquals(intercept,expectedC, 1e-6)

        println("Test passed: Quadratic regression coefficients are correct!")
        println("Computed coefficients: Intercept=$intercept, Linear=$linear, Quadratic=$quadratic")
    }
}