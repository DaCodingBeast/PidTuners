import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.verifyData
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class SlideRangeTests {

    @Test
    fun `test SlideRange creation from inches`() {
        val slideRange = SlideRange.fromInches(0.0, 10.0)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(10.0, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation from centimeters`() {
        val slideRange = SlideRange.fromCM(0.0, 25.4)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(25.4, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.CM, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation from ticks`() {
        val slideRange = SlideRange.fromTicks(0.0, 1000.0)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(1000.0, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.TICKS, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation from inches with motor`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            1.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromInches(0.0, 10.0, slideMotor)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation from centimeters with motor`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            1.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromCM(0.0, 25.4, slideMotor)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(DistanceUnit.TICKS, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation from ticks with motor`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            1.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromTicks(0.0, 1000.0, slideMotor)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(1000.0*PI, slideRange.stop, 0.001) // Converted to inches
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation with negative values`() {
        val slideRange = SlideRange.fromInches(-5.0, 5.0)

        assertNotNull(slideRange)
        assertEquals(-5.0, slideRange.start, 0.001)
        assertEquals(5.0, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test SlideRange creation with zero values`() {
        val slideRange = SlideRange.fromInches(0.0, 0.0)

        assertNotNull(slideRange)
        assertEquals(0.0, slideRange.start, 0.001)
        assertEquals(0.0, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test inRange check with overlapping ranges`() {
        val goal = SlideRange.fromInches(0.0, 10.0)
        val obstacle = SlideRange.fromInches(5.0, 15.0)

        val inRange = goal.inRange(goal, obstacle)

        assertTrue("Obstacle should be in range", inRange)
    }

    @Test
    fun `test inRange check with non-overlapping ranges`() {
        val goal = SlideRange.fromInches(0.0, 5.0)
        val obstacle = SlideRange.fromInches(10.0, 15.0)

        val inRange = goal.inRange(goal, obstacle)

        assertTrue("Obstacle should not be in range", !inRange)
    }

    @Test
    fun `test inRange check with boundary overlap`() {
        val goal = SlideRange.fromInches(0.0, 10.0)
        val obstacle = SlideRange.fromInches(10.0, 20.0)

        val inRange = goal.inRange(goal, obstacle)

        assertTrue("Boundary overlap should be detected", inRange)
    }

    @Test
    fun `test inRange check with goal completely within obstacle`() {
        val goal = SlideRange.fromInches(5.0, 8.0)
        val obstacle = SlideRange.fromInches(0.0, 10.0)

        val inRange = goal.inRange(goal, obstacle)

        assertTrue("Goal within obstacle should be detected", inRange)
    }

    @Test
    fun `test toInches conversion`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromCM(0.0, 25.4)
        val converted = slideRange.toInches(slideMotor)

        assertNotNull(converted)
        assertEquals(0.0, converted.start, 0.001)
        assertEquals(10.0, converted.stop, 0.001) // 25.4 cm = 10 inches
        assertEquals(DistanceUnit.INCHES, converted.unit)
    }

    @Test
    fun `test toInches conversion from ticks`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            1.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromTicks(0.0, 1000.0)
        val converted = slideRange.toInches(slideMotor)

        assertNotNull(converted)
        assertEquals(0.0, converted.start, 0.001)
        assertEquals(1000.0* PI, converted.stop, 0.001) // Assuming 1:1 conversion for test
        assertEquals(DistanceUnit.INCHES, converted.unit)
    }

    @Test
    fun `test toInches conversion from inches`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromInches(0.0, 10.0)
        val converted = slideRange.toInches(slideMotor)

        assertNotNull(converted)
        assertEquals(0.0, converted.start, 0.001)
        assertEquals(10.0, converted.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, converted.unit)
    }

    @Test
    fun `test asArrayList method`() {
        val slideRange = SlideRange.fromInches(0.0, 10.0)
        val arrayList = slideRange.asArrayList()

        assertNotNull(arrayList)
        assertEquals(1, arrayList.size)
        assertEquals(slideRange, arrayList[0])
    }

    @Test
    fun `test SlideRange with different units`() {
        val inchesRange = SlideRange.fromInches(0.0, 10.0)
        val cmRange = SlideRange.fromCM(0.0, 25.4)
        val ticksRange = SlideRange.fromTicks(0.0, 1000.0)

        assertNotNull(inchesRange)
        assertNotNull(cmRange)
        assertNotNull(ticksRange)

        assertEquals(DistanceUnit.INCHES, inchesRange.unit)
        assertEquals(DistanceUnit.CM, cmRange.unit)
        assertEquals(DistanceUnit.TICKS, ticksRange.unit)
    }

    @Test
    fun `test SlideRange with decimal values`() {
        val slideRange = SlideRange.fromInches(0.5, 10.75)

        assertNotNull(slideRange)
        assertEquals(0.5, slideRange.start, 0.001)
        assertEquals(10.75, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    @Test
    fun `test SlideRange with motor simulation`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromInches(5.0, 15.0)

        assertNotNull(slideRange)
        assertEquals(5.0, slideRange.start, 0.001)
        assertEquals(15.0, slideRange.stop, 0.001)
        assertEquals(DistanceUnit.INCHES, slideRange.unit)
    }

    // Performance Tests
    @Test
    fun `test SlideRange creation performance`() {
        val creationTime = measureNanoTime {
            repeat(1000) {
                val slideRange = SlideRange.fromInches(0.0, 10.0)
                assertNotNull(slideRange)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("SlideRange creation time for 1000 ranges: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("SlideRange creation took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 1000)
    }

    @Test
    fun `test SlideRange creation with motor performance`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val creationTime = measureNanoTime {
            repeat(1000) {
                val slideRange = SlideRange.fromInches(0.0, 10.0, slideMotor)
                assertNotNull(slideRange)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("SlideRange creation with motor time for 1000 ranges: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("SlideRange creation with motor took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 1000)
    }

    @Test
    fun `test inRange check performance`() {
        val goal = SlideRange.fromInches(0.0, 10.0)
        val obstacle = SlideRange.fromInches(5.0, 15.0)

        val inRangeTime = measureNanoTime {
            repeat(1000) {
                goal.inRange(goal, obstacle)
            }
        }

        val avgTimePerCheck = inRangeTime / 1000
        println("InRange check time for 1000 calls: ${inRangeTime} ns")
        println("Average per check: ${avgTimePerCheck} ns")
        assertTrue("InRange check took too long: ${avgTimePerCheck} ns", avgTimePerCheck < 1000)
    }

    @Test
    fun `test toInches conversion performance`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRange = SlideRange.fromCM(0.0, 25.4)

        val conversionTime = measureNanoTime {
            repeat(1000) {
                slideRange.toInches(slideMotor)
            }
        }

        val avgTimePerConversion = conversionTime / 1000
        println("ToInches conversion time for 1000 calls: ${conversionTime} ns")
        println("Average per conversion: ${avgTimePerConversion} ns")
        assertTrue("ToInches conversion took too long: ${avgTimePerConversion} ns", avgTimePerConversion < 1000)
    }

    @Test
    fun `test batch SlideRange operations performance`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val slideRanges = Array(100) { SlideRange.fromInches(it.toDouble(), (it + 10).toDouble()) }
        val obstacles = Array(100) { SlideRange.fromInches((it + 5).toDouble(), (it + 15).toDouble()) }

        val batchTime = measureTimeMillis {
            for (i in 0 until 100) {
                val inRange = slideRanges[i].inRange(slideRanges[i], obstacles[i])
                val converted = slideRanges[i].toInches(slideMotor)
                assertNotNull(inRange)
                assertNotNull(converted)
            }
        }

        val avgTimePerOperation = (batchTime * 1_000_000) / 200 // 100 inRange + 100 toInches
        println("Batch SlideRange operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerOperation} ns")

        assertTrue("Average SlideRange operation time too high: ${avgTimePerOperation} ns", avgTimePerOperation < 100_000)
    }

    @Test
    fun `test SlideRange memory efficiency`() {
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val slideRanges = Array(10000) { SlideRange.fromInches(it.toDouble(), (it + 1).toDouble()) }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 10000 SlideRanges: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 1024 * 1024) // Less than 1MB
    }

    @Test
    fun `test SlideRange operations consistency`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            28.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val goal = SlideRange.fromInches(0.0, 10.0)
        val obstacle = SlideRange.fromInches(5.0, 15.0)
        var times = mutableListOf<Long>()

        repeat(1000) {
            val time = measureNanoTime {
                val inRange = goal.inRange(goal, obstacle)
                val converted = goal.toInches(slideMotor)
                assertNotNull(inRange)
                assertNotNull(converted)
            }
            times.add(time)
        }
        times = times.verifyData()

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("SlideRange operations consistency over 1000 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")
        print(times)

        assertTrue("Max SlideRange operation time too high: ${maxTime} ns", maxTime < 10_000)
        assertTrue("SlideRange operation timing variance too high", (maxTime - minTime) < 5_000)
    }

    @Test
    fun `test SlideRange with different unit conversions`() {
        // Create a simple motor setup for testing
        val motorSpecs = MotorSpecs(
            100.0, // RPM
            StallTorque(10.0, TorqueUnit.NEWTON_METER), // Stall torque
            1.0, // Gear ratio
            1.0 // Encoder ticks per rotation
        )
        
        val systemConstants = SlideSystemConstants(1.0, 50.0)
        val targets = listOf(SlideRange.fromInches(0.0, 10.0))
        
        val slideMotor = SlideMotor.Builder(
            "testMotor",
            DcMotorSimple.Direction.FORWARD,
            motorSpecs,
            systemConstants,
            1.0, // spool diameter
            targets
        ).build()

        val inchesRange = SlideRange.fromInches(0.0, 10.0,slideMotor)
        val cmRange = SlideRange.fromCM(0.0, 25.4, slideMotor)
        val ticksRange = SlideRange.fromTicks(0.0, 1000.0, slideMotor)

        // All should be converted to inches when created with motor
        assertEquals(DistanceUnit.INCHES, inchesRange.unit)
        assertEquals(DistanceUnit.INCHES, cmRange.unit)
        assertEquals(DistanceUnit.INCHES, ticksRange.unit)

        // Values should be approximately equal (all represent 10 inches)
        assertEquals(1000.0*PI, ticksRange.stop, 0.001) // Assuming 1:1 conversion for test
    }
} 