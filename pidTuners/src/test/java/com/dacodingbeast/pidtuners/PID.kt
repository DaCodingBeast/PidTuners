import CommonUtilities.SimulatorPIDController
import CommonUtilities.PIDParams
import CommonUtilities.Result
import com.dacodingbeast.pidtuners.Algorithm.Vector
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.verifyData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.PI
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
class PID {

    @Test
    fun `test PIDParams initialization with individual values`() {
        val params = PIDParams(1.0, 0.5, 0.1, 0.2)

        assertEquals(1.0, params.kp, 0.001)
        assertEquals(0.5, params.ki, 0.001)
        assertEquals(0.1, params.kd, 0.001)
        assertEquals(0.2, params.kf, 0.001)
    }

    @Test
    fun `test PIDParams initialization with Vector`() {
        val vector = Vector(doubleArrayOf(2.0, 1.0, 0.5, 0.3))
        val params = PIDParams(vector)

        assertEquals(2.0, params.kp, 0.001)
        assertEquals(1.0, params.ki, 0.001)
        assertEquals(0.5, params.kd, 0.001)
        assertEquals(0.3, params.kf, 0.001)
    }

    @Test
    fun `test PIDParams initialization with Vector without kf`() {
        val vector = Vector(doubleArrayOf(2.0, 1.0, 0.5))
        val params = PIDParams(vector)

        assertEquals(2.0, params.kp, 0.001)
        assertEquals(1.0, params.ki, 0.001)
        assertEquals(0.5, params.kd, 0.001)
        assertEquals(0.0, params.kf, 0.001)
    }

    @Test
    fun `test PIDFController initialization`() {
        val params = PIDParams(1.0, 0.5, 0.1, 0.2)
        val controller = SimulatorPIDController(params)

        assertNotNull(controller)
        assertEquals(params, controller.params)
    }

    @Test
    fun `test SlideRange calculation with zero error`() {
        val params = PIDParams(1.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(5.0, 5.0) // Same start and stop

        val result = controller.calculate(slideRange, null)

        assertNotNull(result)
        assertEquals(0.0, result.error, 0.001)
        assertEquals(0.0, result.motorPower, 0.001)
    }

    @Test
    fun `test SlideRange calculation with positive error`() {
        val params = PIDParams(1.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(0.0, 2.0) // Move forward 2 units

        val result = controller.calculate(slideRange, null)

        assertNotNull(result)
        assertEquals(2.0, result.error, 0.001)
        assertEquals(1.0, result.motorPower, 0.001) // Clamped to max
    }

    @Test
    fun `test SlideRange calculation with negative error`() {
        val params = PIDParams(1.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(5.0, 2.0) // Move backward 3 units

        val result = controller.calculate(slideRange, null)

        assertNotNull(result)
        assertEquals(-3.0, result.error, 0.001)
        assertEquals(-1.0, result.motorPower, 0.001) // Clamped to min
    }

    @Test
    fun `test AngleRange calculation without obstacle`() {
        val params = PIDParams(1.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val angleRange = AngleRange.fromRadians(0.0, PI / 2) // 90 degree turn

        val result = controller.calculate(angleRange, null)

        assertNotNull(result)
        assertTrue("Error should be positive for counter-clockwise movement", result.error > 0.0)
        assertTrue("Motor power should be within bounds", abs(result.motorPower) <= 1.0)
    }

    @Test
    fun `test AngleRange calculation with obstacle`() {
        val params = PIDParams(1.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val angleRange = AngleRange.fromRadians(0.0, PI / 2)
        val obstacle = AngleRange.fromRadians(PI / 4, PI / 3) // Obstacle in the way

        val result = controller.calculate(angleRange, obstacle)

        assertNotNull(result)
        assertNotNull(result.error)
        assertTrue("Motor power should be within bounds", abs(result.motorPower) <= 1.0)
    }

    @Test
    fun `test feedforward term with positive angle`() {
        val params = PIDParams(0.0, 0.0, 0.0, 1.0) // Only feedforward
        val controller = SimulatorPIDController(params)
        val angleRange = AngleRange.fromRadians(PI / 6, PI / 4) // Positive start angle

        val result = controller.calculate(angleRange, null)

        assertNotNull(result)
        assertTrue("Motor power should include feedforward contribution", result.motorPower != 0.0)
    }

    @Test
    fun `test feedforward term with negative angle`() {
        val params = PIDParams(0.0, 0.0, 0.0, 1.0) // Only feedforward
        val controller = SimulatorPIDController(params)
        val angleRange = AngleRange.fromRadians(-PI / 6, -PI / 4) // Negative start angle

        val result = controller.calculate(angleRange, null)

        assertNotNull(result)
        assertTrue("Motor power should include feedforward contribution", result.motorPower != 0.0)
    }

    @Test
    fun `test integral accumulation over multiple calls`() {
        val params = PIDParams(0.0, 1.0, 0.0, 0.0) // Only integral
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(0.0, 1.0) // Constant error

        val result1 = controller.calculate(slideRange, null)
        val result2 = controller.calculate(slideRange, null)

        assertNotNull(result1)
        assertNotNull(result2)
        assertTrue("Integral should accumulate", abs(result2.motorPower) > abs(result1.motorPower))
    }


    @Test
    fun `test motor power clamping`() {
        val params = PIDParams(10.0, 0.0, 0.0, 0.0) // High proportional gain
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(0.0, 10.0) // Large error

        val result = controller.calculate(slideRange, null)

        assertNotNull(result)
        assertEquals(1.0, result.motorPower, 0.001) // Should be clamped to 1.0
        assertEquals(10.0, result.error, 0.001) // Error should remain unclamped
    }

    @Test
    fun `test controller reset functionality`() {
        val params = PIDParams(0.0, 1.0, 1.0, 0.0) // Integral and derivative
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(0.0, 1.0)

        // Run controller to build up integral and set previous error
        controller.calculate(slideRange, null)
        controller.calculate(slideRange, null)

        // Reset controller
        controller.reset()

        // Next calculation should behave as if it's the first
        val resultAfterReset = controller.calculate(slideRange, null)

        assertNotNull(resultAfterReset)
        assertEquals(1.0, resultAfterReset.error, 0.001)
    }

    @Test
    fun `test combined PIDF response`() {
        val params = PIDParams(1.0, 0.1, 0.05, 0.2)
        val controller = SimulatorPIDController(params)
        val angleRange = AngleRange.fromRadians(0.0, PI / 4)

        val result = controller.calculate(angleRange, null)

        assertNotNull(result)
        assertTrue("Motor power should be within bounds", abs(result.motorPower) <= 1.0)
        assertTrue("Error should be reasonable", abs(result.error) <= 2 * PI)
    }

    @Test
    fun `test Result class properties`() {
        val result = Result(0.75, 1.5)

        assertEquals(0.75, result.motorPower, 0.001)
        assertEquals(1.5, result.error, 0.001)
    }

    @Test
    fun `test controller with zero gains`() {
        val params = PIDParams(0.0, 0.0, 0.0, 0.0)
        val controller = SimulatorPIDController(params)
        val slideRange = SlideRange.fromInches(0.0, 5.0)

        val result = controller.calculate(slideRange, null)

        assertNotNull(result)
        assertEquals(0.0, result.motorPower, 0.001)
        assertEquals(5.0, result.error, 0.001)
    }

        @Test
        fun testSingleAngleCalculationTime() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val position = AngleRange.fromRadians(0.0, PI / 4)

            val executionTime = measureNanoTime {
                val result = pidController.calculate(position, null)
                assertNotNull(result)
            }

            println("Single AngleRange calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
            assertTrue("PID calculation took too long: ${executionTime} ns", executionTime < 100_000)
        }

        @Test
        fun testSingleSlideCalculationTime() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val position = SlideRange.fromInches(0.0, 100.0)

            val executionTime = measureNanoTime {
                val result = pidController.calculate(position, null)
                assertNotNull(result)
            }

            println("Single SlideRange calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
            assertTrue("PID slide calculation took too long: ${executionTime} ns", executionTime < 50_000)
        }

        @Test
        fun testFeedforwardCalculationTime() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05, 0.2))
            val position = AngleRange.fromRadians(PI / 6, PI / 3)

            val executionTime = measureNanoTime {
                val result = pidController.calculate(position, null)
                assertNotNull(result)
            }

            println("Feedforward PID calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
            assertTrue("PID with feedforward took too long: ${executionTime} ns", executionTime < 150_000)
        }

        @Test
        fun testCalculationWithObstacle() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val position = AngleRange.fromRadians(0.0, PI / 2)
            val obstacle = AngleRange.fromRadians(PI / 4, PI / 3)

            val executionTime = measureNanoTime {
                val result = pidController.calculate(position, obstacle)
                assertNotNull(result)
            }

            println("PID with obstacle calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
            assertTrue("PID with obstacle took too long: ${executionTime} ns", executionTime < 200_000)
        }

        @Test
        fun testBatch100Calculations() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val positions = mutableListOf<AngleRange>()

            // Generate 100 test positions
            for (i in 0..99) {
                positions.add(AngleRange.fromRadians(i * 0.01, (i + 1) * 0.01))
            }

            val totalTime = measureTimeMillis {
                for (position in positions) {
                    val result = pidController.calculate(position, null)
                    assertNotNull(result)
                }
            }

            val avgTimePerCalculation = (totalTime * 1_000_000) / 100 // Convert to nanoseconds
            println("100 calculations total: ${totalTime} ms")
            println("Average per calculation: ${avgTimePerCalculation} ns")

            assertTrue("Average calculation time too high: ${avgTimePerCalculation} ns",
                avgTimePerCalculation < 100_000)
        }

        @Test
        fun testBatch1000Calculations() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val positions = mutableListOf<SlideRange>()

            // Generate 1000 test positions
            for (i in 0..999) {
                positions.add(SlideRange.fromInches(i.toDouble(), (i + 100).toDouble()))
            }

            val totalTime = measureTimeMillis {
                for (position in positions) {
                    val result = pidController.calculate(position, null)
                    assertNotNull(result)
                }
            }

            val avgTimePerCalculation = (totalTime * 1_000_000) / 1000 // Convert to nanoseconds
            println("1000 calculations total: ${totalTime} ms")
            println("Average per calculation: ${avgTimePerCalculation} ns")

            assertTrue("Average calculation time too high for 1000 iterations: ${avgTimePerCalculation} ns",
                avgTimePerCalculation < 50_000)
        }

        @Test
        fun testHighFrequencyCalculations() {
            val pidController = SimulatorPIDController(PIDParams(2.0, 0.5, 0.1, 0.3))
            val position = AngleRange.fromRadians(0.0, PI / 4)
            val numCalculations = 10000

            val totalTime = measureNanoTime {
                repeat(numCalculations) {
                    val result = pidController.calculate(position, null)
                    assertNotNull(result)
                }
            }

            val avgTimePerCalculation = totalTime / numCalculations
            println("${numCalculations} high-frequency calculations total: ${totalTime / 1_000_000} ms")
            println("Average per calculation: ${avgTimePerCalculation} ns")

            assertTrue("High frequency calculation average too slow: ${avgTimePerCalculation} ns",
                avgTimePerCalculation < 10_000)
        }

        @Test
        fun testVectorConstructorPerformance() {
            val vectorParams = Vector(doubleArrayOf(1.5, 0.2, 0.08, 0.25))

            val constructionTime = measureNanoTime {
                val pidParams = PIDParams(vectorParams)
                val pidController = SimulatorPIDController(pidParams)
                assertNotNull(pidController)
            }

            println("Vector constructor creation time: ${constructionTime} ns")
            assertTrue("Vector constructor too slow: ${constructionTime} ns", constructionTime < 10_000)
        }

        @Test
        fun testResetPerformance() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))

            // Do some calculations to build up state
            val position = AngleRange.fromRadians(0.0, PI / 4)
            repeat(10) {
                pidController.calculate(position, null)
            }

            val resetTime = measureNanoTime {
                pidController.reset()
            }

            println("Reset operation time: ${resetTime} ns")
            assertTrue("Reset operation too slow: ${resetTime} ns", resetTime < 5_000)
        }

        @Test
        fun testSequentialCalculationsWithReset() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val position = AngleRange.fromRadians(0.0, PI / 2)
            val numSequences = 100
            val calculationsPerSequence = 10

            val totalTime = measureTimeMillis {
                repeat(numSequences) {
                    repeat(calculationsPerSequence) {
                        val result = pidController.calculate(position, null)
                        assertNotNull(result)
                    }
                    pidController.reset()
                }
            }

            val totalCalculations = numSequences * calculationsPerSequence
            val avgTimePerCalculation = (totalTime * 1_000_000) / totalCalculations

            println("${totalCalculations} calculations with resets total: ${totalTime} ms")
            println("Average per calculation: ${avgTimePerCalculation} ns")

            assertTrue("Sequential calculations with reset too slow: ${avgTimePerCalculation} ns",
                avgTimePerCalculation < 100_000)
        }

        @Test
        fun testCalculationConsistency() {
            val pidController = SimulatorPIDController(PIDParams(1.0, 0.1, 0.05))
            val position = AngleRange.fromRadians(0.0, PI / 4)
            val numTests = 1000
            var times = mutableListOf<Long>()

            repeat(numTests) {
                pidController.reset()
                val time = measureNanoTime {
                    val result = pidController.calculate(position, null)
                    assertNotNull(result)
                }
                times.add(time)
            }
            times = times.verifyData()

            val avgTime = times.average()
            val maxTime = times.maxOrNull() ?: 0
            val minTime = times.minOrNull() ?: 0

            println("Timing consistency over ${numTests} tests:")
            println("Average: ${avgTime.toLong()} ns")
            println("Min: ${minTime} ns")
            println("Max: ${maxTime} ns")
            println("Range: ${maxTime - minTime} ns")

            assertTrue("Max calculation time too high: ${maxTime} ns", maxTime < 200_000)
            assertTrue("Timing variance too high", (maxTime - minTime) < 150_000)
        }


}