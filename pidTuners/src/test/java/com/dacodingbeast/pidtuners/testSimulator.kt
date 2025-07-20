package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.HardwareSetup.PIDParams
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware.HDHex
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.AngleRange.Companion.fromDegrees
import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Test


class testSimulator {

    val accuracy = 4.0
    val time = 2.0

    var frictionRPM: Double = 68.0
    var inertia: Double = 0.00956980942013831
    var pidParams: PIDParams =
        PIDParams(1.3532270829317032, 0.41997561835081976, 0.644510238591845, 0.08260169361314862)
    var angleRange: AngleRange = fromDegrees(0.0, 45.0)
    var pivotSystemConstants: PivotSystemConstants = PivotSystemConstants(
        inertia,
        frictionRPM,
        GravityModelConstants(-0.6820297006437362, 0.2932062495195341, 0.35586130838651725)
    )
    var armMotor: ArmMotor = ArmMotor.Builder(
        "pivot", DcMotorSimple.Direction.FORWARD, HDHex(
            Hardware.HDHexGearRatios.GR5_1,
            Hardware.HDHexGearRatios.GR3_1,
            Hardware.HDHexGearRatios.GR5_1
        ).motorSpecs, pivotSystemConstants, angleRange.asArrayList()
    )
        .pidParams(pidParams)
        .build()

    @Test
    fun simulatorRun(){
        runSimulator()
    }
    
    fun runSimulator(){
        // Create a test DataLogger that doesn't use Android Log
        DataLogger.create("TestLogger")
        
        // Override the DataLogger methods to avoid Android Log calls
        val originalInstance = DataLogger.instance
        DataLogger.instance = object : DataLogger("TestLogger") {
            override fun logData(data: Any) {
                print(data)
                print("\n")
            }
            override fun logError(data: Any) {
                print(data)
                print("\n")
            }
            override fun logWarning(data: Any) {
                print(data)
                print("\n")
            }
            override fun logDebug(data: Any) {
                print(data)
                print("\n")
            }
        }

        for (i in armMotor.targets.indices) {

            val algorithm = PSO_Optimizer(
                arrayListOf(
                    Ranges(0.0, accuracy),
                    Ranges(0.0, accuracy / 3.5),
                    Ranges(0.0, accuracy / 1.2),
                    Ranges(0.0, accuracy)
                ), time, armMotor, i
            )

            algorithm.update(25)
            val bestParticle = algorithm.getBest()
            println("=== TEST RESULTS ===")
            println("Best PID Parameters: $bestParticle")
            println("Best Fitness Score: ${bestParticle.bestResult}")
            println("===================")
        }

    }


}