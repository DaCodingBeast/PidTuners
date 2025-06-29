import com.dacodingbeast.pidtuners.utilities.MathFunctions.removeOutliers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class RemoveOutliersTests {

    @Test
    fun `test removeOutliers with empty data`() {
        val data = ArrayList<Double>()

        val result = removeOutliers(data)

        assertNotNull(result)
        assertTrue("Result should be empty", result.isEmpty())
    }

    @Test
    fun `test removeOutliers with single value`() {
        val data = ArrayList<Double>().apply { add(5.0) }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(5.0, result[0], 0.001)
    }

    @Test
    fun `test removeOutliers with two values`() {
        val data = ArrayList<Double>().apply { 
            add(5.0)
            add(10.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(5.0, result[0], 0.001)
        assertEquals(10.0, result[1], 0.001)
    }

    @Test
    fun `test removeOutliers with no outliers`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(2.0)
            add(3.0)
            add(4.0)
            add(5.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(5, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertEquals(3.0, result[2], 0.001)
        assertEquals(4.0, result[3], 0.001)
        assertEquals(5.0, result[4], 0.001)
    }

    @Test
    fun `test removeOutliers with one outlier`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(2.0)
            add(3.0)
            add(4.0)
            add(100.0) // Outlier
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(4, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertEquals(3.0, result[2], 0.001)
        assertEquals(4.0, result[3], 0.001)
    }

    @Test
    fun `test removeOutliers with multiple outliers`() {
        val data = ArrayList<Double>().apply { 
            add(-100.0) // Outlier
            add(1.0)
            add(2.0)
            add(3.0)
            add(4.0)
            add(100.0) // Outlier
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(4, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertEquals(3.0, result[2], 0.001)
        assertEquals(4.0, result[3], 0.001)
    }

    @Test
    fun `test removeOutliers with all outliers`() {
        val data = ArrayList<Double>().apply { 
            add(-1000.0)
            add(1000.0)
            add(-500.0)
            add(500.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertTrue("Result should be empty or very small", result.size <= 1)
    }

    @Test
    fun `test removeOutliers with negative values`() {
        val data = ArrayList<Double>().apply { 
            add(-5.0)
            add(-3.0)
            add(-1.0)
            add(1.0)
            add(3.0)
            add(5.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(6, result.size)
        assertEquals(-5.0, result[0], 0.001)
        assertEquals(-3.0, result[1], 0.001)
        assertEquals(-1.0, result[2], 0.001)
        assertEquals(1.0, result[3], 0.001)
        assertEquals(3.0, result[4], 0.001)
        assertEquals(5.0, result[5], 0.001)
    }

    @Test
    fun `test removeOutliers with decimal values`() {
        val data = ArrayList<Double>().apply { 
            add(1.5)
            add(2.3)
            add(3.7)
            add(4.1)
            add(5.9)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(5, result.size)
        assertEquals(1.5, result[0], 0.001)
        assertEquals(2.3, result[1], 0.001)
        assertEquals(3.7, result[2], 0.001)
        assertEquals(4.1, result[3], 0.001)
        assertEquals(5.9, result[4], 0.001)
    }

    @Test
    fun `test removeOutliers with large dataset`() {
        val data = ArrayList<Double>()
        for (i in 1..100) {
            data.add(i.toDouble())
        }
        data.add(1000.0) // Outlier

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(100, result.size)
        assertTrue("Should not contain outlier", !result.contains(1000.0))
    }

    @Test
    fun `test removeOutliers with repeated values`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(1.0)
            add(2.0)
            add(2.0)
            add(3.0)
            add(3.0)
            add(100.0) // Outlier
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(6, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(1.0, result[1], 0.001)
        assertEquals(2.0, result[2], 0.001)
        assertEquals(2.0, result[3], 0.001)
        assertEquals(3.0, result[4], 0.001)
        assertEquals(3.0, result[5], 0.001)
    }

    @Test
    fun `test removeOutliers with boundary values`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(2.0)
            add(3.0)
            add(4.0)
            add(5.0)
            add(6.0)
            add(7.0)
            add(8.0)
            add(9.0)
            add(10.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(10, result.size)
        for (i in 0..9) {
            assertEquals((i + 1).toDouble(), result[i], 0.001)
        }
    }

    @Test
    fun `test removeOutliers with extreme outliers`() {
        val data = ArrayList<Double>().apply { 
            add(Double.MAX_VALUE)
            add(1.0)
            add(2.0)
            add(3.0)
            add(Double.MIN_VALUE)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertEquals(3.0, result[2], 0.001)
    }

    @Test
    fun `test removeOutliers with NaN values`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(2.0)
            add(Double.NaN)
            add(3.0)
            add(4.0)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(5, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertTrue("Should contain NaN", result[2].isNaN())
        assertEquals(3.0, result[3], 0.001)
        assertEquals(4.0, result[4], 0.001)
    }

    @Test
    fun `test removeOutliers with infinity values`() {
        val data = ArrayList<Double>().apply { 
            add(1.0)
            add(2.0)
            add(Double.POSITIVE_INFINITY)
            add(3.0)
            add(Double.NEGATIVE_INFINITY)
        }

        val result = removeOutliers(data)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(1.0, result[0], 0.001)
        assertEquals(2.0, result[1], 0.001)
        assertEquals(3.0, result[2], 0.001)
    }

    // Performance Tests
    @Test
    fun `test removeOutliers performance with small dataset`() {
        val data = ArrayList<Double>().apply { 
            for (i in 1..10) add(i.toDouble())
            add(1000.0) // Outlier
        }

        val outlierTime = measureNanoTime {
            val result = removeOutliers(data)
            assertNotNull(result)
        }

        println("Remove outliers time (small dataset): ${outlierTime} ns")
        assertTrue("Remove outliers took too long: ${outlierTime} ns", outlierTime < 10_000)
    }

    @Test
    fun `test removeOutliers performance with medium dataset`() {
        val data = ArrayList<Double>().apply { 
            for (i in 1..100) add(i.toDouble())
            add(10000.0) // Outlier
        }

        val outlierTime = measureNanoTime {
            val result = removeOutliers(data)
            assertNotNull(result)
        }

        println("Remove outliers time (medium dataset): ${outlierTime} ns")
        assertTrue("Remove outliers took too long: ${outlierTime} ns", outlierTime < 50_000)
    }

    @Test
    fun `test removeOutliers performance with large dataset`() {
        val data = ArrayList<Double>().apply { 
            for (i in 1..1000) add(i.toDouble())
            add(100000.0) // Outlier
        }

        val outlierTime = measureNanoTime {
            val result = removeOutliers(data)
            assertNotNull(result)
        }

        println("Remove outliers time (large dataset): ${outlierTime} ns")
        assertTrue("Remove outliers took too long: ${outlierTime} ns", outlierTime < 200_000)
    }

    @Test
    fun `test batch removeOutliers performance`() {
        val datasets = List(100) { datasetIndex ->
            ArrayList<Double>().apply { 
                for (i in 1..50) add(i.toDouble() + datasetIndex)
                add(10000.0 + datasetIndex) // Outlier
            }
        }

        val totalTime = measureTimeMillis {
            for (data in datasets) {
                val result = removeOutliers(data)
                assertNotNull(result)
            }
        }

        val avgTimePerDataset = (totalTime * 1_000_000) / datasets.size
        println("Batch remove outliers total: ${totalTime} ms")
        println("Average per dataset: ${avgTimePerDataset} ns")

        assertTrue("Average remove outliers time too high: ${avgTimePerDataset} ns",
            avgTimePerDataset < 100_000)
    }

    @Test
    fun `test removeOutliers operations consistency`() {
        val data = ArrayList<Double>().apply { 
            for (i in 1..50) add(i.toDouble())
            add(1000.0) // Outlier
        }
        val times = mutableListOf<Long>()

        repeat(100) {
            val time = measureNanoTime {
                val result = removeOutliers(data)
                assertNotNull(result)
            }
            times.add(time)
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("Remove outliers consistency over 100 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")

        assertTrue("Max remove outliers time too high: ${maxTime} ns", maxTime < 100_000)
        assertTrue("Remove outliers timing variance too high", (maxTime - minTime) < 80_000)
    }

    @Test
    fun `test removeOutliers with different data distributions`() {
        val normalData = ArrayList<Double>().apply { 
            for (i in 1..100) add(i.toDouble())
        }
        val skewedData = ArrayList<Double>().apply { 
            for (i in 1..100) add(i * i.toDouble())
        }
        val uniformData = ArrayList<Double>().apply { 
            for (i in 1..100) add(5.0)
        }

        val totalTime = measureTimeMillis {
            val normalResult = removeOutliers(normalData)
            val skewedResult = removeOutliers(skewedData)
            val uniformResult = removeOutliers(uniformData)
            
            assertNotNull(normalResult)
            assertNotNull(skewedResult)
            assertNotNull(uniformResult)
        }

        println("Different distributions remove outliers time: ${totalTime} ms")
        assertTrue("Remove outliers with different distributions took too long: ${totalTime} ms",
            totalTime < 100)
    }

    @Test
    fun `test removeOutliers memory usage`() {
        val datasets = mutableListOf<ArrayList<Double>>()
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        repeat(100) { datasetIndex ->
            val data = ArrayList<Double>().apply { 
                for (i in 1..50) add(i.toDouble() + datasetIndex)
                add(10000.0 + datasetIndex) // Outlier
            }
            datasets.add(data)
        }

        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        val avgMemoryPerDataset = memoryUsed / 100

        println("Memory used for 100 datasets: ${memoryUsed} bytes")
        println("Average memory per dataset: ${avgMemoryPerDataset} bytes")

        assertTrue("Memory usage per dataset too high: ${avgMemoryPerDataset} bytes",
            avgMemoryPerDataset < 5_000)
    }

    @Test
    fun `test removeOutliers edge cases`() {
        // Test with all same values
        val sameValues = ArrayList<Double>().apply { 
            repeat(10) { add(5.0) }
        }
        val sameResult = removeOutliers(sameValues)
        assertEquals(10, sameResult.size)

        // Test with alternating values
        val alternating = ArrayList<Double>().apply { 
            repeat(10) { add(if (it % 2 == 0) 1.0 else 100.0) }
        }
        val alternatingResult = removeOutliers(alternating)
        assertTrue("Should remove some outliers", alternatingResult.size < 10)

        // Test with single outlier
        val singleOutlier = ArrayList<Double>().apply { 
            repeat(9) { add(5.0) }
            add(1000.0)
        }
        val singleOutlierResult = removeOutliers(singleOutlier)
        assertEquals(9, singleOutlierResult.size)
        assertTrue("Should not contain outlier", !singleOutlierResult.contains(1000.0))
    }
} 