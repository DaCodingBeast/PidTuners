import com.dacodingbeast.pidtuners.utilities.Measurements
import com.dacodingbeast.pidtuners.utilities.AngleUnit
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class MeasurementsTests {

    @Test
    fun `test Angle creation with default unit`() {
        val angle = Measurements.Angle(90.0)

        assertNotNull(angle)
        assertEquals(90.0, angle.number, 0.001)
        assertEquals(AngleUnit.DEGREES, angle.unit)
    }

    @Test
    fun `test Angle creation with explicit unit`() {
        val angle = Measurements.Angle(PI / 2, AngleUnit.RADIANS)

        assertNotNull(angle)
        assertEquals(PI / 2, angle.number, 0.001)
        assertEquals(AngleUnit.RADIANS, angle.unit)
    }

    @Test
    fun `test Angle ofDegrees factory method`() {
        val angle = Measurements.Angle.ofDegrees(90.0)

        assertNotNull(angle)
        assertEquals(90.0, angle.number, 0.001)
        assertEquals(AngleUnit.DEGREES, angle.unit)
    }

    @Test
    fun `test Angle ofRadians factory method`() {
        val angle = Measurements.Angle.ofRadians(PI / 2)

        assertNotNull(angle)
        assertEquals(PI / 2, angle.number, 0.001)
        assertEquals(AngleUnit.RADIANS, angle.unit)
    }

    @Test
    fun `test Angle of factory method`() {
        val angle = Measurements.Angle.of(45.0, AngleUnit.DEGREES)

        assertNotNull(angle)
        assertEquals(45.0, angle.number, 0.001)
        assertEquals(AngleUnit.DEGREES, angle.unit)
    }

    @Test
    fun `test Angle toDegrees conversion from radians`() {
        val angle = Measurements.Angle.ofRadians(PI / 2)
        val degrees = angle.toDegrees()

        assertEquals(90.0, degrees, 0.001)
    }

    @Test
    fun `test Angle toDegrees conversion from degrees`() {
        val angle = Measurements.Angle.ofDegrees(45.0)
        val degrees = angle.toDegrees()

        assertEquals(45.0, degrees, 0.001)
    }

    @Test
    fun `test Angle toRadians conversion from degrees`() {
        val angle = Measurements.Angle.ofDegrees(90.0)
        val radians = angle.toRadians()

        assertEquals(PI / 2, radians, 0.001)
    }

    @Test
    fun `test Angle toRadians conversion from radians`() {
        val angle = Measurements.Angle.ofRadians(PI / 4)
        val radians = angle.toRadians()

        assertEquals(PI / 4, radians, 0.001)
    }

    @Test
    fun `test Angle toAngleUnit conversion to degrees`() {
        val angle = Measurements.Angle.ofRadians(PI / 2)
        val converted = angle.toAngleUnit(AngleUnit.DEGREES)

        assertNotNull(converted)
        assertEquals(90.0, converted.number, 0.001)
        assertEquals(AngleUnit.DEGREES, converted.unit)
    }

    @Test
    fun `test Angle toAngleUnit conversion to radians`() {
        val angle = Measurements.Angle.ofDegrees(90.0)
        val converted = angle.toAngleUnit(AngleUnit.RADIANS)

        assertNotNull(converted)
        assertEquals(PI / 2, converted.number, 0.001)
        assertEquals(AngleUnit.RADIANS, converted.unit)
    }

    @Test
    fun `test Angle wrap method`() {
        val angle = Measurements.Angle.ofRadians(3 * PI / 2)
        val wrapped = angle.wrap()

        assertNotNull(wrapped)
        assertEquals(-PI / 2, wrapped.number, 0.001)
        assertEquals(AngleUnit.RADIANS, wrapped.unit)
    }

    @Test
    fun `test Angle normalize method`() {
        val angle = Measurements.Angle.ofRadians(-PI / 2)
        val normalized = angle.normalize()

        assertEquals(3 * PI / 2, normalized, 0.001)
    }

    @Test
    fun `test Distance creation with default unit`() {
        val distance = Measurements.Distance(10.0)

        assertNotNull(distance)
        assertEquals(10.0, distance.number, 0.001)
        assertEquals(DistanceUnit.INCHES, distance.unit)
    }

    @Test
    fun `test Distance creation with explicit unit`() {
        val distance = Measurements.Distance(25.4, DistanceUnit.CM)

        assertNotNull(distance)
        assertEquals(25.4, distance.number, 0.001)
        assertEquals(DistanceUnit.CM, distance.unit)
    }

    @Test
    fun `test Distance ofInches factory method`() {
        val distance = Measurements.Distance.ofInches(10.0)

        assertNotNull(distance)
        assertEquals(10.0, distance.number, 0.001)
        assertEquals(DistanceUnit.INCHES, distance.unit)
    }

    @Test
    fun `test Distance ofTicks factory method`() {
        val distance = Measurements.Distance.ofTicks(1000.0)

        assertNotNull(distance)
        assertEquals(1000.0, distance.number, 0.001)
        assertEquals(DistanceUnit.TICKS, distance.unit)
    }

    @Test
    fun `test Distance ofCm factory method`() {
        val distance = Measurements.Distance.ofCm(25.4)

        assertNotNull(distance)
        assertEquals(25.4, distance.number, 0.001)
        assertEquals(DistanceUnit.CM, distance.unit)
    }

    @Test
    fun `test Distance of factory method`() {
        val distance = Measurements.Distance.of(5.0, DistanceUnit.INCHES)

        assertNotNull(distance)
        assertEquals(5.0, distance.number, 0.001)
        assertEquals(DistanceUnit.INCHES, distance.unit)
    }

    @Test
    fun `test Distance toInches conversion from inches`() {
        val distance = Measurements.Distance.ofInches(10.0)
        val inches = distance.toInches(1.0) // 1 tick per inch

        assertEquals(10.0, inches, 0.001)
    }

    @Test
    fun `test Distance toInches conversion from centimeters`() {
        val distance = Measurements.Distance.ofCm(25.4)
        val inches = distance.toInches(1.0) // 1 tick per inch

        assertEquals(10.0, inches, 0.001) // 25.4 cm = 10 inches
    }

    @Test
    fun `test Distance toInches conversion from ticks`() {
        val distance = Measurements.Distance.ofTicks(1000.0)
        val inches = distance.toInches(100.0) // 100 ticks per inch

        assertEquals(10.0, inches, 0.001)
    }

    @Test
    fun `test Distance toTicks conversion from inches`() {
        val distance = Measurements.Distance.ofInches(10.0)
        val ticks = distance.toTicks(100.0) // 100 ticks per inch

        assertEquals(1000.0, ticks, 0.001)
    }

    @Test
    fun `test Distance toTicks conversion from centimeters`() {
        val distance = Measurements.Distance.ofCm(25.4)
        val ticks = distance.toTicks(100.0) // 100 ticks per inch

        assertEquals(1000.0, ticks, 0.001) // 25.4 cm = 10 inches = 1000 ticks
    }

    @Test
    fun `test Distance toTicks conversion from ticks`() {
        val distance = Measurements.Distance.ofTicks(1000.0)
        val ticks = distance.toTicks(100.0) // 100 ticks per inch

        assertEquals(1000.0, ticks, 0.001)
    }

    @Test
    fun `test Distance toCm conversion from inches`() {
        val distance = Measurements.Distance.ofInches(10.0)
        val cm = distance.toCm(1.0) // 1 tick per inch

        assertEquals(25.4, cm, 0.001) // 10 inches = 25.4 cm
    }

    @Test
    fun `test Distance toCm conversion from centimeters`() {
        val distance = Measurements.Distance.ofCm(25.4)
        val cm = distance.toCm(1.0) // 1 tick per inch

        assertEquals(25.4, cm, 0.001)
    }

    @Test
    fun `test Distance toCm conversion from ticks`() {
        val distance = Measurements.Distance.ofTicks(1000.0)
        val cm = distance.toCm(100.0) // 100 ticks per inch

        assertEquals(254.0, cm, 0.001) // 1000 ticks = 10 inches = 254 cm
    }

    @Test
    fun `test Distance toDistanceUnit conversion to inches`() {
        val distance = Measurements.Distance.ofCm(25.4)
        val converted = distance.toDistanceUnit(DistanceUnit.INCHES, 1.0)

        assertNotNull(converted)
        assertEquals(10.0, converted.number, 0.001)
        assertEquals(DistanceUnit.INCHES, converted.unit)
    }

    @Test
    fun `test Distance toDistanceUnit conversion to ticks`() {
        val distance = Measurements.Distance.ofInches(10.0)
        val converted = distance.toDistanceUnit(DistanceUnit.TICKS, 100.0)

        assertNotNull(converted)
        assertEquals(1000.0, converted.number, 0.001)
        assertEquals(DistanceUnit.TICKS, converted.unit)
    }

    @Test
    fun `test Distance toDistanceUnit conversion to cm`() {
        val distance = Measurements.Distance.ofInches(10.0)
        val converted = distance.toDistanceUnit(DistanceUnit.CM, 1.0)

        assertNotNull(converted)
        assertEquals(25.4, converted.number, 0.001)
        assertEquals(DistanceUnit.CM, converted.unit)
    }

    @Test
    fun `test AngleUnit preferredInputType`() {
        val preferred = AngleUnit.preferredInputType()

        assertEquals(AngleUnit.DEGREES, preferred)
    }

    @Test
    fun `test DistanceUnit preferredInputType`() {
        val preferred = DistanceUnit.preferredInputType()

        assertEquals(DistanceUnit.INCHES, preferred)
    }

    @Test
    fun `test Angle with negative values`() {
        val angle = Measurements.Angle.ofDegrees(-90.0)
        val radians = angle.toRadians()

        assertEquals(-PI / 2, radians, 0.001)
    }

    @Test
    fun `test Distance with negative values`() {
        val distance = Measurements.Distance.ofInches(-10.0)
        val cm = distance.toCm(1.0)

        assertEquals(-25.4, cm, 0.001)
    }

    @Test
    fun `test Angle with zero values`() {
        val angle = Measurements.Angle.ofDegrees(0.0)
        val radians = angle.toRadians()

        assertEquals(0.0, radians, 0.001)
    }

    @Test
    fun `test Distance with zero values`() {
        val distance = Measurements.Distance.ofInches(0.0)
        val cm = distance.toCm(1.0)

        assertEquals(0.0, cm, 0.001)
    }

    // Performance Tests
    @Test
    fun `test Angle creation performance`() {
        val creationTime = measureNanoTime {
            repeat(1000) {
                val angle = Measurements.Angle.ofDegrees(it.toDouble())
                assertNotNull(angle)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("Angle creation time for 1000 angles: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("Angle creation took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 1000)
    }

    @Test
    fun `test Distance creation performance`() {
        val creationTime = measureNanoTime {
            repeat(1000) {
                val distance = Measurements.Distance.ofInches(it.toDouble())
                assertNotNull(distance)
            }
        }

        val avgTimePerCreation = creationTime / 1000
        println("Distance creation time for 1000 distances: ${creationTime} ns")
        println("Average per creation: ${avgTimePerCreation} ns")
        assertTrue("Distance creation took too long: ${avgTimePerCreation} ns", avgTimePerCreation < 1000)
    }

    @Test
    fun `test Angle conversion performance`() {
        val angle = Measurements.Angle.ofDegrees(90.0)

        val conversionTime = measureNanoTime {
            repeat(1000) {
                angle.toRadians()
                angle.toDegrees()
            }
        }

        val avgTimePerConversion = conversionTime / 2000 // 1000 toRadians + 1000 toDegrees
        println("Angle conversion time for 2000 conversions: ${conversionTime} ns")
        println("Average per conversion: ${avgTimePerConversion} ns")
        assertTrue("Angle conversion took too long: ${avgTimePerConversion} ns", avgTimePerConversion < 1000)
    }

    @Test
    fun `test Distance conversion performance`() {
        val distance = Measurements.Distance.ofInches(10.0)

        val conversionTime = measureNanoTime {
            repeat(1000) {
                distance.toCm(1.0)
                distance.toTicks(100.0)
                distance.toInches(1.0)
            }
        }

        val avgTimePerConversion = conversionTime / 3000 // 1000 of each conversion
        println("Distance conversion time for 3000 conversions: ${conversionTime} ns")
        println("Average per conversion: ${avgTimePerConversion} ns")
        assertTrue("Distance conversion took too long: ${avgTimePerConversion} ns", avgTimePerConversion < 1000)
    }

    @Test
    fun `test Angle wrap performance`() {
        val angle = Measurements.Angle.ofRadians(3 * PI / 2)

        val wrapTime = measureNanoTime {
            repeat(1000) {
                angle.wrap()
            }
        }

        val avgTimePerWrap = wrapTime / 1000
        println("Angle wrap time for 1000 wraps: ${wrapTime} ns")
        println("Average per wrap: ${avgTimePerWrap} ns")
        assertTrue("Angle wrap took too long: ${avgTimePerWrap} ns", avgTimePerWrap < 1000)
    }

    @Test
    fun `test Angle normalize performance`() {
        val angle = Measurements.Angle.ofRadians(-PI / 2)

        val normalizeTime = measureNanoTime {
            repeat(1000) {
                angle.normalize()
            }
        }

        val avgTimePerNormalize = normalizeTime / 1000
        println("Angle normalize time for 1000 normalizes: ${normalizeTime} ns")
        println("Average per normalize: ${avgTimePerNormalize} ns")
        assertTrue("Angle normalize took too long: ${avgTimePerNormalize} ns", avgTimePerNormalize < 1000)
    }

    @Test
    fun `test batch measurements operations performance`() {
        val angles = Array(100) { Measurements.Angle.ofDegrees(it.toDouble()) }
        val distances = Array(100) { Measurements.Distance.ofInches(it.toDouble()) }

        val batchTime = measureTimeMillis {
            for (i in 0 until 100) {
                angles[i].toRadians()
                angles[i].toDegrees()
                distances[i].toCm(1.0)
                distances[i].toTicks(100.0)
            }
        }

        val avgTimePerOperation = (batchTime * 1_000_000) / 400 // 100 each of 4 operations
        println("Batch measurements operations total: ${batchTime} ms")
        println("Average per operation: ${avgTimePerOperation} ns")

        assertTrue("Average measurements operation time too high: ${avgTimePerOperation} ns", avgTimePerOperation < 100_000)
    }

    @Test
    fun `test Measurements memory efficiency`() {
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        
        val angles = Array(10000) { Measurements.Angle.ofDegrees(it.toDouble()) }
        val distances = Array(10000) { Measurements.Distance.ofInches(it.toDouble()) }
        
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("Memory used for 20000 measurements: ${memoryUsed / 1024} KB")
        assertTrue("Memory usage too high: ${memoryUsed / 1024} KB", memoryUsed < 1024 * 1024) // Less than 1MB
    }

    @Test
    fun `test Measurements operations consistency`() {
        val angle = Measurements.Angle.ofDegrees(90.0)
        val distance = Measurements.Distance.ofInches(10.0)
        val times = mutableListOf<Long>()

        repeat(1000) {
            val time = measureNanoTime {
                angle.toRadians()
                distance.toCm(1.0)
            }
            times.add(time)
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0
        val minTime = times.minOrNull() ?: 0

        println("Measurements operations consistency over 1000 tests:")
        println("Average: ${avgTime.toLong()} ns")
        println("Min: ${minTime} ns")
        println("Max: ${maxTime} ns")
        println("Range: ${maxTime - minTime} ns")

        assertTrue("Max measurements operation time too high: ${maxTime} ns", maxTime < 10_000)
        assertTrue("Measurements operation timing variance too high", (maxTime - minTime) < 5_000)
    }

    @Test
    fun `test complex unit conversions`() {
        val angle = Measurements.Angle.ofDegrees(180.0)
        val distance = Measurements.Distance.ofCm(50.8)

        val conversionTime = measureNanoTime {
            repeat(1000) {
                val radians = angle.toRadians()
                val degrees = angle.toDegrees()
                val inches = distance.toInches(1.0)
                val cm = distance.toCm(1.0)
                val ticks = distance.toTicks(100.0)
                
                assertNotNull(radians)
                assertNotNull(degrees)
                assertNotNull(inches)
                assertNotNull(cm)
                assertNotNull(ticks)
            }
        }

        val avgTimePerConversion = conversionTime / 5000 // 1000 * 5 conversions
        println("Complex unit conversions time for 5000 conversions: ${conversionTime} ns")
        println("Average per conversion: ${avgTimePerConversion} ns")
        assertTrue("Complex unit conversion took too long: ${avgTimePerConversion} ns", avgTimePerConversion < 1000)
    }
} 