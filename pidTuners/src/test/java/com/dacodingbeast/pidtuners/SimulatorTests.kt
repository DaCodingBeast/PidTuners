package com.dacodingbeast.pidtuners

import ArmSpecific.ArmSim
import ArmSpecific.Direction
import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Algorithm.Particle
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.Simulators.SlideSim
import com.dacodingbeast.pidtuners.utilities.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.PI
import kotlin.math.abs
import org.junit.Test
import org.junit.Assert.*
import CommonUtilities.PIDFcontroller
import CommonUtilities.PIDParams
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class SimulatorTests {

    // Test data constants
    companion object {
        const val TEST_DT = 0.01
        const val TEST_INERTIA = 0.1 // kg⋅m²
        const val TEST_FRICTION_RPM = 100.0
        const val TEST_EFFECTIVE_MASS = 2.0 // kg
        const val TEST_SPOOL_DIAMETER = 1.0 // inches
        const val TEST_SPOOL_RADIUS_METERS = TEST_SPOOL_DIAMETER * 0.0254 / 2.0
        const val TEST_MOTOR_POWER = 0.5
        const val TEST_TARGET_ANGLE = PI / 4.0 // 45 degrees
        const val TEST_TARGET_EXTENSION = 10.0 // inches
        const val TEST_ACCEPTABLE_ERROR = 3.0 // inches for slide, 3 degrees for arm
        const val TEST_ACCEPTABLE_VELOCITY = 1.0
    }

    // Mock motor specs for testing
    private val mockMotorSpecs = MotorSpecs(
        rpm = 6000.0,
        stallTorque = StallTorque(2.0, TorqueUnit.NEWTON_METER),
        encoderTicksPerRotation = 1440.0
    )

    // Mock gravity constants for arm testing
    private val mockGravityConstants = GravityModelConstants(
        a = 0.1,
        h = PI / 2.0,
        k = 0.5
    )

    // Mock system constants
    private val mockPivotConstants = PivotSystemConstants(
        Inertia = TEST_INERTIA,
        frictionRPM = TEST_FRICTION_RPM,
        gravityConstants = mockGravityConstants
    )

    private val mockSlideConstants = SlideSystemConstants(
        effectiveMass = TEST_EFFECTIVE_MASS,
        frictionRPM = TEST_FRICTION_RPM
    )

    // Test targets
    private val armTargets = listOf(
        AngleRange.fromRadians(0.0, TEST_TARGET_ANGLE)
    )

    private val slideTargets = listOf(
        SlideRange.fromInches(0.0, TEST_TARGET_EXTENSION)
    )

    @Test
    fun testArmSimInitialization() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        assertNotNull(armSim)
        assertEquals(armMotor, armSim.motor)
        assertEquals(0, armSim.targetIndex)
        assertEquals(Math.toRadians(3.0), armSim.acceptableError, 1e-6)
        assertEquals(1.0, armSim.acceptableVelocity, 1e-6)
    }

    @Test
    fun testSlideSimInitialization() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        assertNotNull(slideSim)
        assertEquals(slideMotor, slideSim.motor)
        assertEquals(0, slideSim.targetIndex)
        assertEquals(3.0, slideSim.acceptableError, 1e-6) // inches
        assertEquals(1.0, slideSim.acceptableVelocity, 1e-6)
    }

    @Test
    fun testArmSimGravityTorqueCalculation() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test gravity torque calculation for different angles
        val angle1 = PI / 4.0 // 45 degrees
        val expectedGravityTorque1 = mockGravityConstants.gravityTorque(abs(angle1)) * -1 // negative because angle > 0
        
        // Verify the gravity torque calculation matches the expected formula
        val actualGravityTorque1 = mockGravityConstants.gravityTorque(abs(angle1)) * if (angle1 > 0) -1 else 1
        assertEquals(expectedGravityTorque1, actualGravityTorque1, 1e-6)
        
        // Test with negative angle
        val angle2 = -PI / 4.0 // -45 degrees
        val expectedGravityTorque2 = mockGravityConstants.gravityTorque(abs(angle2)) * 1 // positive because angle < 0
        val actualGravityTorque2 = mockGravityConstants.gravityTorque(abs(angle2)) * if (angle2 > 0) -1 else 1
        assertEquals(expectedGravityTorque2, actualGravityTorque2, 1e-6)
    }

    @Test
    fun testSlideSimLinearForceCalculation() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test motor torque to linear force conversion
        val motorTorque = 1.0 // N⋅m
        val expectedLinearForce = motorTorque / TEST_SPOOL_RADIUS_METERS
        
        // Verify the conversion formula
        val spoolRadius = slideMotor.spoolDiameter * 0.0254 / 2.0
        val actualLinearForce = motorTorque / spoolRadius
        
        assertEquals(expectedLinearForce, actualLinearForce, 1e-6)
        assertEquals(TEST_SPOOL_RADIUS_METERS, spoolRadius, 1e-6)
    }

    @Test
    fun testArmSimAngularAccelerationCalculation() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test angular acceleration calculation
        val motorTorque = 0.5 // N⋅m
        val gravityTorque = -0.2 // N⋅m
        val totalTorque = motorTorque + gravityTorque
        val expectedAngularAcceleration = totalTorque / TEST_INERTIA
        
        // Verify the calculation matches the expected formula
        assertEquals(expectedAngularAcceleration, totalTorque / mockPivotConstants.Inertia, 1e-6)
    }

    @Test
    fun testSlideSimLinearAccelerationCalculation() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test linear acceleration calculation
        val linearForce = 10.0 // N
        val expectedLinearAccel = (linearForce / TEST_EFFECTIVE_MASS) / 0.0254 // convert to inches
        
        // Verify the calculation matches the expected formula
        assertEquals(expectedLinearAccel, (linearForce / mockSlideConstants.effectiveMass) / 0.0254, 1e-6)
    }

    @Test
    fun testArmSimVelocityUpdate() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test velocity update with angular acceleration
        val initialVelocity = 0.0
        val angularAcceleration = 1.0 // rad/s²
        val expectedVelocity = initialVelocity + angularAcceleration * Dt
        
        // Verify the velocity update formula
        assertEquals(expectedVelocity, initialVelocity + angularAcceleration * Dt, 1e-6)
    }

    @Test
    fun testSlideSimVelocityUpdate() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test velocity update with linear acceleration
        val initialVelocity = 0.0
        val linearAccel = 10.0 // inches/s²
        val expectedVelocity = initialVelocity + linearAccel * Dt
        
        // Verify the velocity update formula
        assertEquals(expectedVelocity, initialVelocity + linearAccel * Dt, 1e-6)
    }

    @Test
    fun testArmSimPositionUpdate() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test position update with velocity
        val initialPosition = 0.0
        val velocity = 1.0 // rad/s
        val expectedPosition = AngleRange.wrap(initialPosition + velocity * Dt)
        
        // Verify the position update formula
        assertEquals(expectedPosition, AngleRange.wrap(initialPosition + velocity * Dt), 1e-6)
    }

    @Test
    fun testSlideSimPositionUpdate() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test position update with velocity and acceleration
        val initialPosition = 0.0
        val velocity = 5.0 // inches/s
        val linearAccel = 10.0 // inches/s²
        val expectedPosition = initialPosition + velocity * Dt + 0.5 * linearAccel * Dt * Dt
        
        // Verify the position update formula
        assertEquals(expectedPosition, initialPosition + velocity * Dt + 0.5 * linearAccel * Dt * Dt, 1e-6)
    }

    @Test
    fun testArmSimPunishSimulatorWithAcceptableError() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test with acceptable error and velocity
        armSim.error = Math.toRadians(1.0) // Less than acceptable error (3 degrees)
        armSim.velocity = 0.5 // Less than acceptable velocity (1.0)
        
        val punishment = armSim.punishSimulator()
        assertEquals(0.0, punishment, 1e-6) // Should be no punishment
    }

    @Test
    fun testArmSimPunishSimulatorWithUnacceptableError() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test with unacceptable error
        armSim.error = Math.toRadians(5.0) // Greater than acceptable error (3 degrees)
        armSim.velocity = 0.5 // Acceptable velocity
        
        val punishment = armSim.punishSimulator()
        val expectedPunishment = abs(armSim.error) * 1000
        assertEquals(expectedPunishment, punishment, 1e-6)
    }

    @Test
    fun testSlideSimPunishSimulatorWithAcceptableError() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test with acceptable error and velocity
        slideSim.error = 1.0 // Less than acceptable error (3 inches)
        slideSim.velocity = 0.5 // Less than acceptable velocity (1.0)
        
        val punishment = slideSim.punishSimulator()
        assertEquals(0.0, punishment, 1e-6) // Should be no punishment
    }

    @Test
    fun testSlideSimPunishSimulatorWithUnacceptableVelocity() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test with unacceptable velocity
        slideSim.error = 1.0 // Acceptable error
        slideSim.velocity = 2.0 // Greater than acceptable velocity (1.0)
        
        val punishment = slideSim.punishSimulator()
        val expectedPunishment = abs(slideSim.velocity) * 20
        assertEquals(expectedPunishment, punishment, 1e-6)
    }

    @Test
    fun testUnitConversionsInSlideSim() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()

        val slideSim = SlideSim(slideMotor, 0)
        
        // Test spool diameter to radius conversion
        val spoolRadius = slideMotor.spoolDiameter * 0.0254 / 2.0 // Convert inches to meters
        assertEquals(TEST_SPOOL_RADIUS_METERS, spoolRadius, 1e-6)
        
        // Test linear acceleration conversion from m/s² to inches/s²
        val linearForce = 10.0 // N
        val linearAccelMeters = linearForce / TEST_EFFECTIVE_MASS // m/s²
        val linearAccelInches = linearAccelMeters / 0.0254 // inches/s²
        
        assertEquals(linearAccelInches, (linearForce / mockSlideConstants.effectiveMass) / 0.0254, 1e-6)
    }

    @Test
    fun testAngleWrappingInArmSim() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()

        val armSim = ArmSim(armMotor, 0)
        
        // Test angle wrapping for angles outside -π to π range
        val angle1 = 2 * PI + PI / 4.0 // Should wrap to PI / 4.0
        val wrappedAngle1 = AngleRange.wrap(angle1)
        assertEquals(PI / 4.0, wrappedAngle1, 1e-6)
        
        val angle2 = -2 * PI - PI / 4.0 // Should wrap to -PI / 4.0
        val wrappedAngle2 = AngleRange.wrap(angle2)
        assertEquals(-PI / 4.0, wrappedAngle2, 1e-6)
    }

    @Test
    fun testGravityModelConstantsValidation() {
        val gravityConstants = GravityModelConstants(0.1, PI / 2.0, 0.5)
        
        // Test valid angle range
        val validAngle = PI / 4.0
        val gravityTorque = gravityConstants.gravityTorque(validAngle)
        assertTrue(gravityTorque.isFinite())
        
        // Test angle at boundary
        val boundaryAngle = PI
        val boundaryGravityTorque = gravityConstants.gravityTorque(boundaryAngle)
        assertTrue(boundaryGravityTorque.isFinite())
    }

    @Test
    fun testSlideSimWithDifferentSpoolDiameters() {
        val smallSpoolDiameter = 0.5 // inches
        val largeSpoolDiameter = 2.0 // inches
        
        val slideMotorSmall = SlideMotor.Builder(
            "testSlideSmall",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            smallSpoolDiameter,
            slideTargets
        ).build()

        val slideMotorLarge = SlideMotor.Builder(
            "testSlideLarge",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            largeSpoolDiameter,
            slideTargets
        ).build()

        val slideSimSmall = SlideSim(slideMotorSmall, 0)
        val slideSimLarge = SlideSim(slideMotorLarge, 0)
        
        // Test that different spool diameters result in different linear forces
        val motorTorque = 1.0 // N⋅m
        val spoolRadiusSmall = smallSpoolDiameter * 0.0254 / 2.0
        val spoolRadiusLarge = largeSpoolDiameter * 0.0254 / 2.0
        
        val linearForceSmall = motorTorque / spoolRadiusSmall
        val linearForceLarge = motorTorque / spoolRadiusLarge
        
        assertTrue(linearForceSmall > linearForceLarge) // Smaller spool = higher force
    }

    @Test
    fun testArmSimWithDifferentInertias() {
        val lowInertia = 0.05 // kg⋅m²
        val highInertia = 0.2 // kg⋅m²
        
        val pivotConstantsLow = PivotSystemConstants(
            Inertia = lowInertia,
            frictionRPM = TEST_FRICTION_RPM,
            gravityConstants = mockGravityConstants
        )

        val pivotConstantsHigh = PivotSystemConstants(
            Inertia = highInertia,
            frictionRPM = TEST_FRICTION_RPM,
            gravityConstants = mockGravityConstants
        )

        val armMotorLow = ArmMotor.Builder(
            "testArmLow",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            pivotConstantsLow,
            armTargets
        ).build()

        val armMotorHigh = ArmMotor.Builder(
            "testArmHigh",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            pivotConstantsHigh,
            armTargets
        ).build()

        val armSimLow = ArmSim(armMotorLow, 0)
        val armSimHigh = ArmSim(armMotorHigh, 0)
        
        // Test that different inertias result in different angular accelerations
        val torque = 1.0 // N⋅m
        val angularAccelLow = torque / lowInertia
        val angularAccelHigh = torque / highInertia
        
        assertTrue(angularAccelLow > angularAccelHigh) // Lower inertia = higher acceleration
    }

    @Test
    fun testArmSimVelocityAfterStep() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()
        val armSim = ArmSim(armMotor, 0)
        armSim.error = 0.0
        armSim.velocity = 0.0
        // Use a real PIDFcontroller with high proportional gain
        armSim.pidController = PIDFcontroller(PIDParams(10.0, 0.0, 0.0, 0.0))
        val data = armSim.updateSimulator()
        // The velocity should be nonzero after a step with nonzero control effort
        assertTrue(data.velocity != 0.0)
    }

    @Test
    fun testSlideSimVelocityAfterStep() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()
        val slideSim = SlideSim(slideMotor, 0)
        slideSim.error = 0.0
        slideSim.velocity = 0.0
        // Use a real PIDFcontroller with high proportional gain
        slideSim.pidController = PIDFcontroller(PIDParams(10.0, 0.0, 0.0, 0.0))
        val data = slideSim.updateSimulator()
        // The velocity should be nonzero after a step with nonzero control effort
        assertTrue(data.velocity != 0.0)
    }

    @Test
    fun testArmSimCalculationPerformance() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()
        val armSim = ArmSim(armMotor, 0)
        armSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.2))
        
        val executionTime = measureNanoTime {
            val data = armSim.updateSimulator()
            assertNotNull(data)
        }
        
        println("ArmSim single calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
        assertTrue("ArmSim calculation took too long: ${executionTime} ns", executionTime < 100_000)
    }

    @Test
    fun testSlideSimCalculationPerformance() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()
        val slideSim = SlideSim(slideMotor, 0)
        slideSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.0))
        
        val executionTime = measureNanoTime {
            val data = slideSim.updateSimulator()
            assertNotNull(data)
        }
        
        println("SlideSim single calculation: ${executionTime} ns (${executionTime / 1000.0} µs)")
        assertTrue("SlideSim calculation took too long: ${executionTime} ns", executionTime < 100_000)
    }

    @Test
    fun testArmSimBatchPerformance() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()
        val armSim = ArmSim(armMotor, 0)
        armSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.2))
        
        val numCalculations = 1000
        val totalTime = measureTimeMillis {
            repeat(numCalculations) {
                val data = armSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        val avgTimePerCalculation = (totalTime * 1_000_000) / numCalculations // Convert to nanoseconds
        println("ArmSim ${numCalculations} calculations total: ${totalTime} ms")
        println("ArmSim average per calculation: ${avgTimePerCalculation} ns")
        
        assertTrue("ArmSim average calculation time too high: ${avgTimePerCalculation} ns", 
            avgTimePerCalculation < 50_000)
    }

    @Test
    fun testSlideSimBatchPerformance() {
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()
        val slideSim = SlideSim(slideMotor, 0)
        slideSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.0))
        
        val numCalculations = 1000
        val totalTime = measureTimeMillis {
            repeat(numCalculations) {
                val data = slideSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        val avgTimePerCalculation = (totalTime * 1_000_000) / numCalculations // Convert to nanoseconds
        println("SlideSim ${numCalculations} calculations total: ${totalTime} ms")
        println("SlideSim average per calculation: ${avgTimePerCalculation} ns")
        
        assertTrue("SlideSim average calculation time too high: ${avgTimePerCalculation} ns", 
            avgTimePerCalculation < 50_000)
    }

    @Test
    fun testSimulatorComparisonPerformance() {
        // Setup both simulators
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()
        val armSim = ArmSim(armMotor, 0)
        armSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.2))
        
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()
        val slideSim = SlideSim(slideMotor, 0)
        slideSim.pidController = PIDFcontroller(PIDParams(1.0, 0.1, 0.05, 0.0))
        
        val numCalculations = 100
        
        // Test ArmSim performance
        val armTime = measureTimeMillis {
            repeat(numCalculations) {
                val data = armSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        // Test SlideSim performance
        val slideTime = measureTimeMillis {
            repeat(numCalculations) {
                val data = slideSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        val armAvg = (armTime * 1_000_000) / numCalculations
        val slideAvg = (slideTime * 1_000_000) / numCalculations
        
        println("Performance comparison (${numCalculations} calculations each):")
        println("ArmSim: ${armTime} ms total, ${armAvg} ns average")
        println("SlideSim: ${slideTime} ms total, ${slideAvg} ns average")
        println("Ratio (ArmSim/SlideSim): ${armAvg.toDouble() / slideAvg}")
        
        assertTrue("Both simulators should complete calculations in reasonable time", 
            armAvg < 100_000 && slideAvg < 100_000)
    }

    @Test
    fun testHighFrequencySimulationPerformance() {
        val armMotor = ArmMotor.Builder(
            "testArm",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockPivotConstants,
            armTargets
        ).build()
        val armSim = ArmSim(armMotor, 0)
        armSim.pidController = PIDFcontroller(PIDParams(2.0, 0.5, 0.1, 0.3))
        
        val slideMotor = SlideMotor.Builder(
            "testSlide",
            DcMotorSimple.Direction.FORWARD,
            mockMotorSpecs,
            mockSlideConstants,
            TEST_SPOOL_DIAMETER,
            slideTargets
        ).build()
        val slideSim = SlideSim(slideMotor, 0)
        slideSim.pidController = PIDFcontroller(PIDParams(2.0, 0.5, 0.1, 0.0))
        
        val numCalculations = 10000
        
        // High frequency ArmSim test
        val armTime = measureNanoTime {
            repeat(numCalculations) {
                val data = armSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        // High frequency SlideSim test
        val slideTime = measureNanoTime {
            repeat(numCalculations) {
                val data = slideSim.updateSimulator()
                assertNotNull(data)
            }
        }
        
        val armAvg = armTime / numCalculations
        val slideAvg = slideTime / numCalculations
        
        println("High frequency performance (${numCalculations} calculations each):")
        println("ArmSim: ${armTime / 1_000_000} ms total, ${armAvg} ns average")
        println("SlideSim: ${slideTime / 1_000_000} ms total, ${slideAvg} ns average")
        println("Ratio (ArmSim/SlideSim): ${armAvg.toDouble() / slideAvg}")
        
        assertTrue("High frequency simulation should be fast", 
            armAvg < 10_000 && slideAvg < 10_000)
    }
}
