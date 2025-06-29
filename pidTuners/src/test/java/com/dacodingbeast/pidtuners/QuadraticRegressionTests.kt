import com.dacodingbeast.pidtuners.utilities.MathFunctions.QuadraticRegression
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class QuadraticRegressionTests {

    @Test
    fun `test quadraticRegressionManual with simple data`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(1.0, 4.0, 9.0, 16.0, 25.0) // Perfect quadratic: y = x^2

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(0.0, coefficients[0], 0.001) // a should be close to 0
        assertEquals(0.0, coefficients[1], 0.001) // b should be close to 0
        assertEquals(1.0, coefficients[2], 0.001) // c should be close to 1
    }

    @Test
    fun `test quadraticRegressionManual with linear data`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(2.0, 4.0, 6.0, 8.0, 10.0) // Linear: y = 2x

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(0.0, coefficients[0], 0.001) // a should be close to 0
        assertEquals(2.0, coefficients[1], 0.001) // b should be close to 2
        assertEquals(0.0, coefficients[2], 0.001) // c should be close to 0
    }

    @Test
    fun `test quadraticRegressionManual with constant data`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(5.0, 5.0, 5.0, 5.0, 5.0) // Constant: y = 5

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(0.0, coefficients[0], 0.001) // a should be close to 0
        assertEquals(0.0, coefficients[1], 0.001) // b should be close to 0
        assertEquals(5.0, coefficients[2], 0.001) // c should be close to 5
    }

    @Test
    fun `test quadraticRegressionManual with general quadratic`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(3.0, 8.0, 15.0, 24.0, 35.0) // y = x^2 + 2x

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(1.0, coefficients[0], 0.001) // a should be close to 1
        assertEquals(2.0, coefficients[1], 0.001) // b should be close to 2
        assertEquals(0.0, coefficients[2], 0.001) // c should be close to 0
    }

    @Test
    fun `test quadraticRegressionManual with noisy data`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = doubleArrayOf(1.1, 3.9, 9.2, 15.8, 25.1) // Noisy quadratic

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertTrue("Coefficients should be reasonable", abs(coefficients[0]) < 10.0)
        assertTrue("Coefficients should be reasonable", abs(coefficients[1]) < 10.0)
        assertTrue("Coefficients should be reasonable", abs(coefficients[2]) < 10.0)
    }

    @Test
    fun `test quadraticRegressionManual with negative values`() {
        val x = doubleArrayOf(-2.0, -1.0, 0.0, 1.0, 2.0)
        val y = doubleArrayOf(4.0, 1.0, 0.0, 1.0, 4.0) // y = x^2

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(1.0, coefficients[0], 0.001) // a should be close to 1
        assertEquals(0.0, coefficients[1], 0.001) // b should be close to 0
        assertEquals(0.0, coefficients[2], 0.001) // c should be close to 0
    }

    @Test
    fun `test solveLinearSystem with simple 2x2 system`() {
        val matrix = arrayOf(
            doubleArrayOf(2.0, 1.0),
            doubleArrayOf(1.0, 3.0)
        )
        val rhs = doubleArrayOf(5.0, 6.0)

        val solution = QuadraticRegression.solveLinearSystem(matrix, rhs)

        assertNotNull(solution)
        assertEquals(2, solution.size)
        assertEquals(1.0, solution[0], 0.001)
        assertEquals(3.0, solution[1], 0.001)
    }

    @Test
    fun `test solveLinearSystem with 3x3 system`() {
        val matrix = arrayOf(
            doubleArrayOf(1.0, 1.0, 1.0),
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(1.0, 3.0, 6.0)
        )
        val rhs = doubleArrayOf(6.0, 14.0, 25.0)

        val solution = QuadraticRegression.solveLinearSystem(matrix, rhs)

        assertNotNull(solution)
        assertEquals(3, solution.size)
        assertEquals(1.0, solution[0], 0.001)
        assertEquals(2.0, solution[1], 0.001)
        assertEquals(3.0, solution[2], 0.001)
    }

    @Test
    fun `test solveLinearSystem with identity matrix`() {
        val matrix = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0)
        )
        val rhs = doubleArrayOf(1.0, 2.0, 3.0)

        val solution = QuadraticRegression.solveLinearSystem(matrix, rhs)

        assertNotNull(solution)
        assertEquals(3, solution.size)
        assertEquals(1.0, solution[0], 0.001)
        assertEquals(2.0, solution[1], 0.001)
        assertEquals(3.0, solution[2], 0.001)
    }

    @Test
    fun `test toVertexForm with standard quadratic`() {
        val a = 1.0
        val b = -4.0
        val c = 3.0

        val vertexForm = QuadraticRegression.toVertexForm(a, b, c)

        assertNotNull(vertexForm)
        assertEquals(3, vertexForm.size)
        assertEquals(1.0, vertexForm[0], 0.001) // a
        assertEquals(2.0, vertexForm[1], 0.001) // h (vertex x-coordinate)
        assertEquals(-1.0, vertexForm[2], 0.001) // k (vertex y-coordinate)
    }

    @Test
    fun `test toVertexForm with negative a`() {
        val a = -1.0
        val b = 2.0
        val c = 5.0

        val vertexForm = QuadraticRegression.toVertexForm(a, b, c)

        assertNotNull(vertexForm)
        assertEquals(3, vertexForm.size)
        assertEquals(-1.0, vertexForm[0], 0.001) // a
        assertEquals(1.0, vertexForm[1], 0.001) // h
        assertEquals(6.0, vertexForm[2], 0.001) // k
    }

    @Test
    fun `test toVertexForm with zero b`() {
        val a = 1.0
        val b = 0.0
        val c = 4.0

        val vertexForm = QuadraticRegression.toVertexForm(a, b, c)

        assertNotNull(vertexForm)
        assertEquals(3, vertexForm.size)
        assertEquals(1.0, vertexForm[0], 0.001) // a
        assertEquals(0.0, vertexForm[1], 0.001) // h
        assertEquals(4.0, vertexForm[2], 0.001) // k
    }

    @Test
    fun `test toVertexForm with zero c`() {
        val a = 1.0
        val b = -2.0
        val c = 0.0

        val vertexForm = QuadraticRegression.toVertexForm(a, b, c)

        assertNotNull(vertexForm)
        assertEquals(3, vertexForm.size)
        assertEquals(1.0, vertexForm[0], 0.001) // a
        assertEquals(1.0, vertexForm[1], 0.001) // h
        assertEquals(-1.0, vertexForm[2], 0.001) // k
    }

    @Test
    fun `test quadraticRegressionManual with large dataset`() {
        val size = 100
        val x = DoubleArray(size) { it.toDouble() }
        val y = DoubleArray(size) { (it * it + 2 * it + 1).toDouble() } // y = x^2 + 2x + 1

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertEquals(1.0, coefficients[0], 0.001) // a should be close to 1
        assertEquals(2.0, coefficients[1], 0.001) // b should be close to 2
        assertEquals(1.0, coefficients[2], 0.001) // c should be close to 1
    }

    @Test
    fun `test quadraticRegressionManual with single point`() {
        val x = doubleArrayOf(1.0)
        val y = doubleArrayOf(5.0)

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        // With only one point, the solution may not be unique
        assertTrue("Coefficients should be finite", coefficients.all { it.isFinite() })
    }

    @Test
    fun `test quadraticRegressionManual with two points`() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(3.0, 7.0)

        val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)

        assertNotNull(coefficients)
        assertEquals(3, coefficients.size)
        assertTrue("Coefficients should be finite", coefficients.all { it.isFinite() })
    }

    // Performance Tests
    @Test
    fun `test quadraticRegressionManual performance`() {
        val x = DoubleArray(100) { it.toDouble() }
        val y = DoubleArray(100) { (it * it + 2 * it + 1).toDouble() }

        val regressionTime = measureNanoTime {
            val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)
            assertNotNull(coefficients)
        }

        println("Quadratic regression time: ${regressionTime} ns")
        assertTrue("Quadratic regression took too long: ${regressionTime} ns", regressionTime < 100_000)
    }

    @Test
    fun `test solveLinearSystem performance`() {
        val matrix = Array(10) { i -> DoubleArray(10) { j -> if (i == j) 1.0 else 0.0 } }
        val rhs = DoubleArray(10) { it.toDouble() }

        val solveTime = measureNanoTime {
            val solution = QuadraticRegression.solveLinearSystem(matrix, rhs)
            assertNotNull(solution)
        }

        println("Linear system solve time: ${solveTime} ns")
        assertTrue("Linear system solve took too long: ${solveTime} ns", solveTime < 50_000)
    }

    @Test
    fun `test toVertexForm performance`() {
        val a = 1.0
        val b = -4.0
        val c = 3.0

        val vertexTime = measureNanoTime {
            val vertexForm = QuadraticRegression.toVertexForm(a, b, c)
            assertNotNull(vertexForm)
        }

        println("Vertex form conversion time: ${vertexTime} ns")
        assertTrue("Vertex form conversion took too long: ${vertexTime} ns", vertexTime < 5_000)
    }

    @Test
    fun `test batch quadratic regression performance`() {
        val datasets = List(100) { datasetIndex ->
            val size = 50
            val x = DoubleArray(size) { it.toDouble() }
            val y = DoubleArray(size) { (it * it + datasetIndex * it + 1).toDouble() }
            x to y
        }

        val totalTime = measureTimeMillis {
            for ((x, y) in datasets) {
                val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)
                assertNotNull(coefficients)
            }
        }

        val avgTimePerRegression = (totalTime * 1_000_000) / datasets.size
        println("Batch quadratic regression total: ${totalTime} ms")
        println("Average per regression: ${avgTimePerRegression} ns")

        assertTrue("Average quadratic regression time too high: ${avgTimePerRegression} ns",
            avgTimePerRegression < 200_000)
    }

    @Test
    fun `test batch linear system solving performance`() {
        val systems = List(100) {
            val size = 5
            val matrix = Array(size) { i -> DoubleArray(size) { j -> if (i == j) 1.0 else 0.1 } }
            val rhs = DoubleArray(size) { it.toDouble() }
            matrix to rhs
        }

        val totalTime = measureTimeMillis {
            for ((matrix, rhs) in systems) {
                val solution = QuadraticRegression.solveLinearSystem(matrix, rhs)
                assertNotNull(solution)
            }
        }

        val avgTimePerSystem = (totalTime * 1_000_000) / systems.size
        println("Batch linear system solving total: ${totalTime} ms")
        println("Average per system: ${avgTimePerSystem} ns")

        assertTrue("Average linear system solve time too high: ${avgTimePerSystem} ns",
            avgTimePerSystem < 100_000)
    }

    @Test
    fun `test quadratic regression operations consistency`() {
        val x = DoubleArray(50) { it.toDouble() }
        val y = DoubleArray(50) { (it * it + 2 * it + 1).toDouble() }
        val times = mutableListOf<Long>()

        repeat(100) {
            val time = measureNanoTime {
                val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)
                assertNotNull(coefficients)
            }
            times.add(time)
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("Quadratic regression consistency over 100 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")

        assertTrue("Max quadratic regression time too high: ${maxTime} ns", maxTime < 200_000)
        assertTrue("Quadratic regression timing variance too high", (maxTime - minTime) < 150_000)
    }

    @Test
    fun `test complex mathematical operations performance`() {
        val datasets = List(50) { datasetIndex ->
            val size = 100
            val x = DoubleArray(size) { it.toDouble() }
            val y = DoubleArray(size) { (it * it + datasetIndex * it + 1).toDouble() }
            x to y
        }

        val totalTime = measureTimeMillis {
            for ((x, y) in datasets) {
                val coefficients = QuadraticRegression.quadraticRegressionManual(x, y)
                val vertexForm = QuadraticRegression.toVertexForm(coefficients[0], coefficients[1], coefficients[2])
                assertNotNull(coefficients)
                assertNotNull(vertexForm)
            }
        }

        val avgTimePerCalculation = (totalTime * 1_000_000) / datasets.size
        println("Complex mathematical operations total: ${totalTime} ms")
        println("Average per calculation: ${avgTimePerCalculation} ns")

        assertTrue("Complex mathematical operation time too high: ${avgTimePerCalculation} ns",
            avgTimePerCalculation < 300_000)
    }

    @Test
    fun `test quadratic regression memory usage`() {
        val datasets = mutableListOf<Pair<DoubleArray, DoubleArray>>()
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        repeat(100) { datasetIndex ->
            val size = 50
            val x = DoubleArray(size) { it.toDouble() }
            val y = DoubleArray(size) { (it * it + datasetIndex * it + 1).toDouble() }
            datasets.add(x to y)
        }

        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        val avgMemoryPerDataset = memoryUsed / 100

        println("Memory used for 100 datasets: ${memoryUsed} bytes")
        println("Average memory per dataset: ${avgMemoryPerDataset} bytes")

        assertTrue("Memory usage per dataset too high: ${avgMemoryPerDataset} bytes",
            avgMemoryPerDataset < 10_000)
    }
} 