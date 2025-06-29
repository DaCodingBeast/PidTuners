import com.dacodingbeast.pidtuners.Simulators.AngleRange
import ArmSpecific.Direction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class AngleRangeTests {

    @Test
    fun `test AngleRange creation from radians`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)

        assertNotNull(angleRange)
        assertEquals(0.0, angleRange.start, 0.001)
        assertEquals(PI / 2, angleRange.stop, 0.001)
    }

    @Test
    fun `test AngleRange creation from degrees`() {
        val angleRange = AngleRange.fromDegrees(0.0, 90.0)

        assertNotNull(angleRange)
        assertEquals(0.0, angleRange.start, 0.001)
        assertEquals(PI / 2, angleRange.stop, 0.001)
    }

    @Test
    fun `test AngleRange creation with negative angles`() {
        val angleRange = AngleRange.fromRadians(-PI / 2, PI / 2)

        assertNotNull(angleRange)
        assertEquals(-PI / 2, angleRange.start, 0.001)
        assertEquals(PI / 2, angleRange.stop, 0.001)
    }

    @Test
    fun `test AngleRange creation with large angles`() {
        val angleRange = AngleRange.fromRadians(0.0, 2 * PI)

        assertNotNull(angleRange)
        assertEquals(0.0, angleRange.start, 0.001)
        assertEquals(2 * PI, angleRange.stop, 0.001)
    }

    @Test
    fun `test angle wrapping with positive angles`() {
        val wrapped = AngleRange.wrap(3 * PI)

        assertEquals(PI, wrapped, 0.001)
    }

    @Test
    fun `test angle wrapping with negative angles`() {
        val wrapped = AngleRange.wrap(-3 * PI)

        assertEquals(PI, wrapped, 0.001)
    }

    @Test
    fun `test angle wrapping with angles in range`() {
        val wrapped = AngleRange.wrap(PI / 2)

        assertEquals(PI / 2, wrapped, 0.001)
    }

    @Test
    fun `test angle wrapping with zero`() {
        val wrapped = AngleRange.wrap(0.0)

        assertEquals(0.0, wrapped, 0.001)
    }

    @Test
    fun `test angle normalization with positive angle`() {
        val normalized = AngleRange.normalizeAngle(PI / 2)

        assertEquals(PI / 2, normalized, 0.001)
    }

    @Test
    fun `test angle normalization with negative angle`() {
        val normalized = AngleRange.normalizeAngle(-PI / 2)

        assertEquals(3 * PI / 2, normalized, 0.001)
    }

    @Test
    fun `test angle normalization with zero`() {
        val normalized = AngleRange.normalizeAngle(0.0)

        assertEquals(0.0, normalized, 0.001)
    }

    @Test
    fun `test motor direction calculation without obstacle`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val direction = AngleRange.findMotorDirection(goal, null)

        assertEquals(Direction.CounterClockWise, direction)
    }

    @Test
    fun `test motor direction calculation with obstacle`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)
        val direction = AngleRange.findMotorDirection(goal, obstacle)

        assertEquals(Direction.Clockwise, direction)
    }

    @Test
    fun `test motor direction calculation with negative angle change`() {
        val goal = AngleRange.fromRadians(PI / 2, 0.0)
        val direction = AngleRange.findMotorDirection(goal, null)

        assertEquals(Direction.Clockwise, direction)
    }

    @Test
    fun `test inRange check with overlapping ranges`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)

        val inRange = AngleRange.inRange(goal, obstacle)

        assertTrue("Obstacle should be in range", inRange)
    }

    @Test
    fun `test inRange check with non-overlapping ranges`() {
        val goal = AngleRange.fromRadians(0.0, PI / 4)
        val obstacle = AngleRange.fromRadians(PI / 2, 3 * PI / 4)

        val inRange = AngleRange.inRange(goal, obstacle)

        assertTrue("Obstacle should not be in range", !inRange)
    }

    @Test
    fun `test inRange check with boundary overlap`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 2, PI)

        val inRange = AngleRange.inRange(goal, obstacle)

        assertTrue("Boundary overlap should be detected", inRange)
    }

    @Test
    fun `test PIDF angle error calculation for counter-clockwise direction`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)
        val error = AngleRange.findPIDFAngleError(Direction.CounterClockWise, angleRange)

        assertEquals(PI / 2, error, 0.001)
    }

    @Test
    fun `test PIDF angle error calculation for clockwise direction`() {
        val angleRange = AngleRange.fromRadians(PI / 2, 0.0)
        val error = AngleRange.findPIDFAngleError(Direction.Clockwise, angleRange)

        assertEquals(-PI / 2, error, 0.001)
    }

    @Test
    fun `test PIDF angle error calculation with large angle change`() {
        val angleRange = AngleRange.fromRadians(0.0, 3 * PI / 2)
        val error = AngleRange.findPIDFAngleError(Direction.CounterClockWise, angleRange)

        assertEquals(3 * PI / 2, error, 0.001)
    }

    @Test
    fun `test findDirectionAndError without obstacle`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val (direction, error) = AngleRange.findDirectionAndError(goal, null)

        assertEquals(Direction.CounterClockWise, direction)
        assertEquals(PI / 2, error, 0.001)
    }

    @Test
    fun `test findDirectionAndError with obstacle`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)
        val (direction, error) = AngleRange.findDirectionAndError(goal, obstacle)

        assertEquals(Direction.Clockwise, direction)
        assertEquals(-3 * PI / 2, error, 0.001)
    }

    @Test
    fun `test findDirectionAndError with negative angle change`() {
        val goal = AngleRange.fromRadians(PI / 2, 0.0)
        val (direction, error) = AngleRange.findDirectionAndError(goal, null)

        assertEquals(Direction.Clockwise, direction)
        assertEquals(-PI / 2, error, 0.001)
    }

    @Test
    fun `test toString method`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)
        val result = angleRange.toString()

        assertNotNull(result)
        assertTrue("String should contain angle values", result.contains("0.0") && result.contains("1.5707963267948966"))
    }

    @Test
    fun `test asArrayList method`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)
        val arrayList = angleRange.asArrayList()

        assertNotNull(arrayList)
        assertEquals(1, arrayList.size)
        assertEquals(angleRange, arrayList[0])
    }

    @Test
    fun `test asList method`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)
        val list = angleRange.asList()

        assertNotNull(list)
        assertEquals(1, list.size)
        assertEquals(angleRange, list[0])
    }

    // Performance Tests
    @Test
    fun `test AngleRange creation performance`() {
        val creationTime = measureNanoTime {
            val angleRange = AngleRange.fromRadians(0.0, PI / 2)
            assertNotNull(angleRange)
        }

        println("AngleRange creation time: ${creationTime} ns")
        assertTrue("AngleRange creation took too long: ${creationTime} ns", creationTime < 10_000)
    }

    @Test
    fun `test angle wrapping performance`() {
        val wrappingTime = measureNanoTime {
            repeat(1000) {
                AngleRange.wrap(it * PI / 100)
            }
        }

        val avgTimePerWrap = wrappingTime / 1000
        println("Angle wrapping time for 1000 angles: ${wrappingTime} ns")
        println("Average per wrap: ${avgTimePerWrap} ns")
        assertTrue("Angle wrapping took too long: ${avgTimePerWrap} ns", avgTimePerWrap < 100)
    }

    @Test
    fun `test angle normalization performance`() {
        val normalizationTime = measureNanoTime {
            repeat(1000) {
                AngleRange.normalizeAngle(it * PI / 100)
            }
        }

        val avgTimePerNormalize = normalizationTime / 1000
        println("Angle normalization time for 1000 angles: ${normalizationTime} ns")
        println("Average per normalize: ${avgTimePerNormalize} ns")
        assertTrue("Angle normalization took too long: ${avgTimePerNormalize} ns", avgTimePerNormalize < 100)
    }

    @Test
    fun `test motor direction calculation performance`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)

        val directionTime = measureNanoTime {
            repeat(1000) {
                AngleRange.findMotorDirection(goal, obstacle)
            }
        }

        val avgTimePerDirection = directionTime / 1000
        println("Motor direction calculation time for 1000 calls: ${directionTime} ns")
        println("Average per calculation: ${avgTimePerDirection} ns")
        assertTrue("Motor direction calculation took too long: ${avgTimePerDirection} ns", avgTimePerDirection < 1000)
    }

    @Test
    fun `test inRange check performance`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)

        val inRangeTime = measureNanoTime {
            repeat(1000) {
                AngleRange.inRange(goal, obstacle)
            }
        }

        val avgTimePerCheck = inRangeTime / 1000
        println("InRange check time for 1000 calls: ${inRangeTime} ns")
        println("Average per check: ${avgTimePerCheck} ns")
        assertTrue("InRange check took too long: ${avgTimePerCheck} ns", avgTimePerCheck < 1000)
    }

    @Test
    fun `test PIDF angle error calculation performance`() {
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)

        val errorTime = measureNanoTime {
            repeat(1000) {
                AngleRange.findPIDFAngleError(Direction.CounterClockWise, angleRange)
            }
        }

        val avgTimePerError = errorTime / 1000
        println("PIDF angle error calculation time for 1000 calls: ${errorTime} ns")
        println("Average per calculation: ${avgTimePerError} ns")
        assertTrue("PIDF angle error calculation took too long: ${avgTimePerError} ns", avgTimePerError < 1000)
    }

    @Test
    fun `test findDirectionAndError performance`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)

        val combinedTime = measureNanoTime {
            repeat(1000) {
                AngleRange.findDirectionAndError(goal, obstacle)
            }
        }

        val avgTimePerCombined = combinedTime / 1000
        println("FindDirectionAndError time for 1000 calls: ${combinedTime} ns")
        println("Average per calculation: ${avgTimePerCombined} ns")
        assertTrue("FindDirectionAndError took too long: ${avgTimePerCombined} ns", avgTimePerCombined < 2000)
    }

    @Test
    fun `test batch angle operations performance`() {
        val angleRanges = Array(100) { AngleRange.fromRadians(it * 0.01, (it + 1) * 0.01) }
        val obstacles = Array(100) { AngleRange.fromRadians(it * 0.02, (it + 1) * 0.02) }

        val batchTime = measureTimeMillis {
            for (i in 0 until 100) {
                val (direction, error) = AngleRange.findDirectionAndError(angleRanges[i], obstacles[i])
                assertNotNull(direction)
                assertNotNull(error)
            }
        }

        val avgTimePerOperation = (batchTime * 1_000_000) / 100
        println("Batch angle operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerOperation} ns")

        assertTrue("Average angle operation time too high: ${avgTimePerOperation} ns", avgTimePerOperation < 100_000)
    }

    @Test
    fun `test AngleRange memory efficiency`() {
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val angleRanges = Array(10000) { AngleRange.fromRadians(it * 0.01, (it + 1) * 0.01) }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 10000 AngleRanges: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 1024 * 1024) // Less than 1MB
    }

    @Test
    fun `test AngleRange operations consistency`() {
        val goal = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)
        val times = mutableListOf<Long>()

        repeat(1000) {
            val time = measureNanoTime {
                val (direction, error) = AngleRange.findDirectionAndError(goal, obstacle)
                assertNotNull(direction)
                assertNotNull(error)
            }
            times.add(time)
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("AngleRange operations consistency over 1000 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")

        assertTrue("Max AngleRange operation time too high: ${maxTime} ns", maxTime < 10_000)
        assertTrue("AngleRange operation timing variance too high", (maxTime - minTime) < 5_000)
    }
} 