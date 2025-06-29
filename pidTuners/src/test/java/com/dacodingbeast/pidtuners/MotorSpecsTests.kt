import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class MotorSpecsTests {

    @Test
    fun `test MotorSpecs initialization with all parameters`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 2.0, 1440.0)

        assertNotNull(motorSpecs)
        assertEquals(100.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(2.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(1440.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs initialization with default gear ratio`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)

        assertNotNull(motorSpecs)
        assertEquals(100.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(1.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(1440.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with different torque units`() {
        val stallTorque = StallTorque(100.0, TorqueUnit.NEWTON_METER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)

        assertNotNull(motorSpecs)
        assertEquals(100.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(1.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(1440.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with high RPM`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(10000.0, stallTorque, 1440.0)

        assertNotNull(motorSpecs)
        assertEquals(10000.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(1.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(1440.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with high encoder ticks`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 10000.0)

        assertNotNull(motorSpecs)
        assertEquals(100.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(1.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(10000.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with decimal values`() {
        val stallTorque = StallTorque(10.5, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.5, stallTorque, 2.5, 1440.5)

        assertNotNull(motorSpecs)
        assertEquals(100.5, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(2.5, motorSpecs.motorGearRatio, 0.001)
        assertEquals(1440.5, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test applyGearRatio with positive ratio`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1.0, 1440.0)

        motorSpecs.applyGearRatio(2.0)

        assertEquals(50.0, motorSpecs.rpm, 0.001) // RPM should be halved
        assertEquals(2.0, motorSpecs.motorGearRatio, 0.001) // Gear ratio should be doubled
        assertEquals(20.0, motorSpecs.stallTorque.value, 0.001) // Torque should be doubled
        assertEquals(2880.0, motorSpecs.encoderTicksPerRotation, 0.001) // Ticks should be doubled
    }

    @Test
    fun `test applyGearRatio with fractional ratio`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1.0, 1440.0)

        motorSpecs.applyGearRatio(0.5)

        assertEquals(200.0, motorSpecs.rpm, 0.001) // RPM should be doubled
        assertEquals(0.5, motorSpecs.motorGearRatio, 0.001) // Gear ratio should be halved
        assertEquals(5.0, motorSpecs.stallTorque.value, 0.001) // Torque should be halved
        assertEquals(720.0, motorSpecs.encoderTicksPerRotation, 0.001) // Ticks should be halved
    }

    @Test
    fun `test applyGearRatio multiple times`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1.0, 1440.0)

        motorSpecs.applyGearRatio(2.0)
        motorSpecs.applyGearRatio(3.0)

        assertEquals(16.67, motorSpecs.rpm, 0.01) // 100 / (2 * 3)
        assertEquals(6.0, motorSpecs.motorGearRatio, 0.001) // 1 * 2 * 3
        assertEquals(60.0, motorSpecs.stallTorque.value, 0.001) // 10 * 2 * 3
        assertEquals(8640.0, motorSpecs.encoderTicksPerRotation, 0.001) // 1440 * 2 * 3
    }

    @Test
    fun `test MotorSpecs data class properties`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 2.0, 1440.0)

        // Test that properties can be modified
        motorSpecs.rpm = 200.0
        motorSpecs.motorGearRatio = 3.0
        motorSpecs.encoderTicksPerRotation = 2880.0

        assertEquals(200.0, motorSpecs.rpm, 0.001)
        assertEquals(3.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(2880.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with different torque units conversion`() {
        val stallTorque = StallTorque(1.0, TorqueUnit.NEWTON_METER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)

        assertNotNull(motorSpecs)
        // The stallTorque should be converted to KILOGRAM_CENTIMETER in init
        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, motorSpecs.stallTorque.unit)
    }

    @Test
    fun `test MotorSpecs with very small values`() {
        val stallTorque = StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(1.0, stallTorque, 0.1, 10.0)

        assertNotNull(motorSpecs)
        assertEquals(1.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(0.1, motorSpecs.motorGearRatio, 0.001)
        assertEquals(10.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    @Test
    fun `test MotorSpecs with very large values`() {
        val stallTorque = StallTorque(1000.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100000.0, stallTorque, 100.0, 100000.0)

        assertNotNull(motorSpecs)
        assertEquals(100000.0, motorSpecs.rpm, 0.001)
        assertEquals(stallTorque, motorSpecs.stallTorque)
        assertEquals(100.0, motorSpecs.motorGearRatio, 0.001)
        assertEquals(100000.0, motorSpecs.encoderTicksPerRotation, 0.001)
    }

    // Performance Tests
    @Test
    fun `test MotorSpecs creation performance`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)

        val creationTime = measureNanoTime {
            repeat(1000) {
                val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)
                assertNotNull(motorSpecs)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("MotorSpecs creation time for 1000 instances: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("MotorSpecs creation took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 10_000)
    }

    @Test
    fun `test applyGearRatio performance`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)

        val gearRatioTime = measureNanoTime {
            repeat(1000) {
                motorSpecs.applyGearRatio(2.0)
                motorSpecs.applyGearRatio(0.5) // Reset to original
            }
        }

        val avgTimePerGearRatio = gearRatioTime / 2000 // 1000 * 2 operations
        println("ApplyGearRatio time for 2000 operations: ${gearRatioTime} ns")
        println("Average per operation: ${avgTimePerGearRatio} ns")
        assertTrue("ApplyGearRatio took too long: ${avgTimePerGearRatio} ns", avgTimePerGearRatio < 10_000)
    }

    @Test
    fun `test MotorSpecs batch operations performance`() {
        val stallTorques = Array(100) { StallTorque(it.toDouble(), TorqueUnit.KILOGRAM_CENTIMETER) }

        val batchTime = measureTimeMillis {
            for (i in 0 until 100) {
                val motorSpecs = MotorSpecs(100.0 + i, stallTorques[i], 1440.0 + i)
                motorSpecs.applyGearRatio(2.0)
                assertNotNull(motorSpecs)
            }
        }

        val avgTimePerOperation = (batchTime * 1_000_000) / 200 // 100 creations + 100 gear ratios
        println("Batch MotorSpecs operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerOperation} ns")

        assertTrue("Average MotorSpecs operation time too high: ${avgTimePerOperation} ns", 
            avgTimePerOperation < 100_000)
    }

    @Test
    fun `test MotorSpecs memory efficiency`() {
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val motorSpecs = Array(10000) { 
            MotorSpecs(100.0, StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER), 1440.0) 
        }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 10000 MotorSpecs: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 1024 * 1024) // Less than 1MB
    }

    @Test
    fun `test complex MotorSpecs calculations`() {
        val stallTorque = StallTorque(10.0, TorqueUnit.KILOGRAM_CENTIMETER)
        val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)

        val calculationTime = measureNanoTime {
            repeat(1000) {
                motorSpecs.applyGearRatio(2.0)
                motorSpecs.applyGearRatio(3.0)
                motorSpecs.applyGearRatio(0.5)
                motorSpecs.applyGearRatio(0.33) // Reset to approximately original
            }
        }

        val avgTimePerCalculation = calculationTime / 4000 // 1000 * 4 operations
        println("Complex MotorSpecs calculations time for 4000 operations: ${calculationTime} ns")
        println("Average per operation: ${avgTimePerCalculation} ns")
        assertTrue("Complex MotorSpecs calculations took too long: ${avgTimePerCalculation} ns", 
            avgTimePerCalculation < 10_000)
    }

    @Test
    fun `test MotorSpecs with different torque units performance`() {
        val torqueUnits = arrayOf(
            TorqueUnit.KILOGRAM_CENTIMETER,
            TorqueUnit.NEWTON_METER,
            TorqueUnit.OUNCE_INCH
        )

        val conversionTime = measureNanoTime {
            repeat(1000) {
                for (unit in torqueUnits) {
                    val stallTorque = StallTorque(10.0, unit)
                    val motorSpecs = MotorSpecs(100.0, stallTorque, 1440.0)
                    assertNotNull(motorSpecs)
                }
            }
        }

        val avgTimePerConversion = conversionTime / 3000 // 1000 * 3 units
        println("MotorSpecs with different torque units time for 3000 operations: ${conversionTime} ns")
        println("Average per operation: ${avgTimePerConversion} ns")
        assertTrue("MotorSpecs with different torque units took too long: ${avgTimePerConversion} ns", 
            avgTimePerConversion < 10_000)
    }
} 