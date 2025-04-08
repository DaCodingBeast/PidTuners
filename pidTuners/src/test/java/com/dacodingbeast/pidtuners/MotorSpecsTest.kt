package com.dacodingbeast.pidtuners

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.*
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MotorSpecsTest {

    val simpleMotor = ArmMotor.Builder(
        name = "motor",
        motorDirection = DcMotorSimple.Direction.FORWARD,
        motorSpecs = MotorSpecs(200.0, StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0),
        systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
        targets = AngleRange.fromDegrees(0.0, 90.0).asList()
    )
        .externalGearRatio(1.0)
        .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
        .build()

    val spurMotor = ArmMotor.Builder(
        name = "motor",
        motorDirection = DcMotorSimple.Direction.FORWARD,
        motorSpecs = Hardware.REVSpurMotor.GR20,
        systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
        targets = AngleRange.fromDegrees(0.0, 90.0).asList()
    )
        .externalGearRatio(1.0)
        .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
        .externalEncoder(null)
        .build()

    val yellowJacket = ArmMotor.Builder(
        name = "motor",
        motorDirection = DcMotorSimple.Direction.FORWARD,
        motorSpecs = Hardware.YellowJacket.RPM223,
        systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
        targets = AngleRange.fromDegrees(0.0, 90.0).asList()
    )
        .externalGearRatio(1.0)
        .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
        .externalEncoder(null)
        .build()

    val hdHex = ArmMotor.Builder(
        name = "motor",
        motorDirection = DcMotorSimple.Direction.FORWARD,
        motorSpecs = Hardware.HDHex(Hardware.HDHexGearRatios.GR3_1, Hardware.HDHexGearRatios.GR4_1).motorSpecs,
        systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
        targets = AngleRange.fromDegrees(0.0, 90.0).asList()
    )
        .externalGearRatio(1.0)
        .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
        .externalEncoder(null)
        .build()

    @Test
    fun testMotorSpecs() {
        testSpur()
        testYellowJacket()
        testHDHex()
    }

    fun testSpur() {
        assertEquals(300.0, spurMotor.getRPM())
        assertEquals(20.0, spurMotor.getGearRatio())
        assertEquals(1.4000000000000001, spurMotor.getTicksPerRotation())
        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, spurMotor.motorSpecs.stallTorque.unit)
    }

    fun testYellowJacket() {
        assertEquals(223.0, yellowJacket.getRPM())
        assertEquals(((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(), yellowJacket.getGearRatio())
        assertEquals(751.8, yellowJacket.getTicksPerRotation())
    }

    fun testHDHex() {
        assertEquals(12.0, hdHex.getGearRatio())
        assertEquals(6000 * 1 / 12.0, hdHex.getRPM())
        assertEquals(28.0 * 12, hdHex.getTicksPerRotation())
        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, hdHex.motorSpecs.stallTorque.unit)
        val stall = StallTorque(0.105, TorqueUnit.NEWTON_METER)
        stall.to(TorqueUnit.KILOGRAM_CENTIMETER)
        assertEquals(stall.value * 12, hdHex.motorSpecs.stallTorque.value)
    }

    @Test
    fun testExternalGearRatio() {
        val motor = ArmMotor.Builder(
            name = "motor",
            motorDirection = DcMotorSimple.Direction.FORWARD,
            motorSpecs = MotorSpecs(200.0, StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0),
            systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
            targets = AngleRange.fromDegrees(0.0, 90.0).asList()
        )
            .externalGearRatio(2.0)
            .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
            .externalEncoder(null)
            .build()

        assertEquals(100.0, motor.getRPM())
        assertEquals(8.0, motor.getGearRatio())
        assertEquals(56.0, motor.getTicksPerRotation())
        assertEquals(0.20000000000000004, motor.getStallTorque())
    }

    @Test
    fun testEncoder() {
        val motor = ArmMotor.Builder(
            name = "motor",
            motorDirection = DcMotorSimple.Direction.FORWARD,
            motorSpecs = MotorSpecs(200.0, StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0),
            systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
            targets = AngleRange.fromDegrees(0.0, 90.0).asList()
        )
            .externalGearRatio(1.0)
            .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
            .externalEncoder(DigitalEncoder("encoder", DcMotorSimple.Direction.FORWARD))
            .build()

        assertEquals(motor.getGearRatio(), 1.0)
        assertEquals(motor.getRPM(), 200.0)

        val motor2 = ArmMotor.Builder(
            name = "motor",
            motorDirection = DcMotorSimple.Direction.FORWARD,
            motorSpecs = MotorSpecs(200.0, StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0),
            systemConstants = PivotSystemConstants(0.0, 0.0, GravityModelConstants(0.0, 0.0, 0.0)),
            targets = AngleRange.fromDegrees(0.0, 90.0).asList()
        )
            .externalGearRatio(1.0)
            .pidParams(PIDParams(0.0, 0.0, 0.0, 0.0))
            .externalEncoder(AnalogEncoder("encoder", listOf(Operation(Operand.MULTIPLY, 1.0))))
            .build()

        assertEquals(motor2.getGearRatio(), 1.0)
        assertEquals(motor2.getRPM(), 200.0)
    }
}
