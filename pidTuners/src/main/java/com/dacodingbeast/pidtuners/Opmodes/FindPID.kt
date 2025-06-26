package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

//@TeleOp
class FindPID(val motor: Motors) :
    LinearOpMode() {

        companion object{
            @JvmField var accuracy: Double = 4.0
            @JvmField var Time: Double = 2.5
        }

    override fun runOpMode() {
        DataLogger.instance.startLogger("FindPID" + motor.name)

        waitForStart()

        for (i in motor.targets.indices) {

            val algorithm = PSO_Optimizer(
                arrayListOf(
                    Ranges(0.0, accuracy),
                    Ranges(0.0, accuracy / 3.5),
                    Ranges(0.0, accuracy/ 1.2),
                    Ranges(0.0, accuracy)
                ), Time, motor, i
            )

            algorithm.update(25)
            telemetry.addLine(algorithm.getBest().toString())

            telemetry.update()
            DataLogger.instance.logDebug("Best: " + algorithm.getBest().toString())
        }
    }
}