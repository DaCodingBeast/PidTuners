package com.dacodingbeast.pidtuners

import CommonUtilities.PIDParams
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.AnalogEncoder
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.DigitalEncoder
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.Operand
import com.dacodingbeast.pidtuners.HardwareSetup.Operation
import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.qualcomm.robotcore.hardware.DcMotorSimple
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MotorSpecsTest {
    val simpleMotor = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
        StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), PivotSystemConstants(0.0,0.0,
        GravityModelConstants(0.0,0.0,0.0)
    ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null)
        val spurMotor = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, Hardware.REVSpurMotor.GR20, PivotSystemConstants(0.0,0.0,
            GravityModelConstants(0.0,0.0,0.0)
        ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null)
val yellowJacket = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,PivotSystemConstants(0.0,0.0,
    GravityModelConstants(0.0,0.0,0.0)
), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null)
    val hdHex = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, Hardware.HDHex(Hardware.HDHexGearRatios.GR3_1,
        Hardware.HDHexGearRatios.GR4_1
    ).motorSpecs,PivotSystemConstants(0.0,0.0,
        GravityModelConstants(0.0,0.0,0.0)
    ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null)
    @Test
    fun create_Motor_Spec_bjects(){
        Hardware.REVSpurMotor.GR20
        Hardware.REVSpurMotor.GR40
        Hardware.REVCoreHex.CoreHexMotor
        Hardware.TorqueNado.MAX
        Hardware.NeveRest.Classic_40
        Hardware.NeveRest.Classic_60
        Hardware.NeveRest.Orbital_3_7
        Hardware.NeveRest.Orbital_20
        Hardware.HDHex(Hardware.HDHexGearRatios.GR3_1,Hardware.HDHexGearRatios.GR4_1,Hardware.HDHexGearRatios.GR5_1).motorSpecs
    }
    @Test
    fun testMotorSpecs(){
        testSpur()
        testYellowJacket()
        testHDHex()
    }
    fun testSpur(){
        assertEquals(300.0, spurMotor.getRPM())
        assertEquals(20.0, spurMotor.getGearRatio())
        assertEquals(1.4000000000000001, spurMotor.getTicksPerRotation())
        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, spurMotor.motorSpecs.stallTorque.unit)
    }
    fun testYellowJacket(){
        assertEquals(223.0, yellowJacket.getRPM())
        assertEquals(((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(), yellowJacket.getGearRatio())
        assertEquals(751.8, yellowJacket.getTicksPerRotation())
    }
    fun testHDHex(){
        assertEquals(12.0,hdHex.getGearRatio())
        assertEquals(6000*1/12.0, hdHex.getRPM())
        assertEquals(28.0*12, hdHex.getTicksPerRotation())
        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, hdHex.motorSpecs.stallTorque.unit)
        var stall = StallTorque(0.105,TorqueUnit.NEWTON_METER)
        stall.to(TorqueUnit.KILOGRAM_CENTIMETER)
        assertEquals(stall.value*12, hdHex.motorSpecs.stallTorque.value)
    }
    @Test
    fun testExternalGearRatio(){
        val motor = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
            StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), PivotSystemConstants(0.0,0.0,
            GravityModelConstants(0.0,0.0,0.0)
        ), 2.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null)
        assertEquals(100.0, motor.getRPM())
        assertEquals(8.0, motor.getGearRatio())
        assertEquals(56.0, motor.getTicksPerRotation())
        assertEquals(0.20000000000000004, motor.getStallTorque())

        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), -1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 0.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 0.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), -1.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 0.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    200.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, -28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    0.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArmMotor(
                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
                    -300.0,
                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0
                ), PivotSystemConstants(0.0,0.0,
                    GravityModelConstants(0.0,0.0,0.0)
                ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(),null
            )
        }
    }
    @Test
    fun testEncoder(){
        val motor = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
            StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), PivotSystemConstants(0.0,0.0,
            GravityModelConstants(0.0,0.0,0.0)
        ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(), DigitalEncoder("encoder", DcMotorSimple.Direction.FORWARD))
        assertEquals(motor.getGearRatio() , 1.0)
        assertEquals(motor.getRPM(), 200.0)
        val motor2 = ArmMotor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
            StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), PivotSystemConstants(0.0,0.0,
            GravityModelConstants(0.0,0.0,0.0)
        ), 1.0, PIDParams(0.0,0.0,0.0,0.0),AngleRange.fromDegrees(0.0,90.0).asList(), AnalogEncoder("encoder", listOf(Operation(Operand.MULTIPLY,1.0))))
        assertEquals(motor2.getGearRatio() , 1.0)
        assertEquals(motor2.getRPM(), 200.0)
    }
}