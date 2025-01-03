//package com.dacodingbeast.pidtuners
//
//import com.dacodingbeast.pidtuners.HardwareSetup.Encoder
//import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
//import com.dacodingbeast.pidtuners.HardwareSetup.Motor
//import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
//import com.dacodingbeast.pidtuners.HardwareSetup.torque.StallTorque
//import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit
//import com.qualcomm.robotcore.hardware.DcMotorSimple
//import junit.framework.TestCase.assertEquals
//import org.junit.Assert.assertThrows
//import org.junit.Test
//
//class MotorSpecsTest {
//    val simpleMotor = Motor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
//        StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), 1.0, null)
//        val spurMotor = Motor("motor", DcMotorSimple.Direction.FORWARD, Hardware.REVSpurMotor.`20_1`, 1.0, null)
//val yellowJacket = Motor("motor", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223, 1.0, null)
//    val hdHex = Motor("motor", DcMotorSimple.Direction.FORWARD, Hardware.HDHex(Hardware.HDHexGearRatios.`3_1`,
//        Hardware.HDHexGearRatios.`4_1`).motorSpecs
//        , 1.0, null)
//    @Test
//    fun create_Motor_Spec_bjects(){
//        Hardware.REVSpurMotor.`20_1`
//        Hardware.REVSpurMotor.`40_1`
//        Hardware.REVCoreHex.CoreHexMotor
//        Hardware.TorqueNado.MAX
//        Hardware.NeveRest.Classic_40
//        Hardware.NeveRest.Classic_60
//        Hardware.NeveRest.Orbital_3_7
//        Hardware.NeveRest.Orbital_20
//        Hardware.HDHex(Hardware.HDHexGearRatios.`3_1`,Hardware.HDHexGearRatios.`4_1`,Hardware.HDHexGearRatios.`5_1`).motorSpecs
//    }
//    @Test
//    fun testMotorSpecs(){
//        testSpur()
//        testYellowJacket()
//        testHDHex()
//    }
//    fun testSpur(){
//        assertEquals(300.0, spurMotor.getRPM())
//        assertEquals(20.0, spurMotor.getGearRatio())
//        assertEquals(1.4000000000000001, spurMotor.getTicksPerRotation())
//        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, spurMotor.specs.stallTorque.unit)
//    }
//    fun testYellowJacket(){
//        assertEquals(223.0, yellowJacket.getRPM())
//        assertEquals(((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(), yellowJacket.getGearRatio())
//        assertEquals(751.8, yellowJacket.getTicksPerRotation())
//    }
//    fun testHDHex(){
//        assertEquals(12.0,hdHex.getGearRatio())
//        assertEquals(6000*1/12.0, hdHex.getRPM())
//        assertEquals(28.0*12, hdHex.getTicksPerRotation())
//        assertEquals(TorqueUnit.KILOGRAM_CENTIMETER, hdHex.specs.stallTorque.unit)
//        var stall = StallTorque(0.105,TorqueUnit.NEWTON_METER)
//        stall.to(TorqueUnit.KILOGRAM_CENTIMETER)
//        assertEquals(stall.value*12, hdHex.specs.stallTorque.value)
//    }
//    @Test
//    fun testExternalGearRatio(){
//        val motor = Motor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
//            StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), 2.0, null)
//        assertEquals(100.0, motor.getRPM())
//        assertEquals(8.0, motor.getGearRatio())
//        assertEquals(56.0, motor.getTicksPerRotation())
//        assertEquals(0.20000000000000004, motor.getStallTorque())
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0
//                ), -1.0, null
//            )
//        }
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 4.0, 28.0
//                ), 0.0, null
//            )
//        }
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 0.0, 28.0
//                ), 1.0, null
//            )
//        }
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), -1.0, 28.0
//                ), 1.0, null
//            )
//        }
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 0.0
//                ), 1.0, null
//            )
//        }
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    200.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, -28.0
//                ), 1.0, null
//            )
//        }
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    0.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0
//                ), 1.0, null
//            )
//        }
//        assertThrows(IllegalArgumentException::class.java) {
//            Motor(
//                "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(
//                    -300.0,
//                    StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0
//                ), 1.0, null
//            )
//        }
//    }
//    @Test
//    fun testEncoder(){
//        val motor = Motor("motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(200.0,
//            StallTorque(0.1,TorqueUnit.KILOGRAM_CENTIMETER),4.0,28.0), 1.0, Encoder("encoder", DcMotorSimple.Direction.FORWARD))
//        assertEquals(motor.getGearRatio() , 1.0)
//        assertEquals(motor.getRPM(), 200.0)
//    }
//}