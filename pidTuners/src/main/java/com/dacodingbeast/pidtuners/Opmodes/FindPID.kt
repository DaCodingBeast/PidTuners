package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Constants.Constants
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous
class FindPID(val constants: Constants, private val accuracy:Double, private val sim:SimulatorType, private val Time:Double) : LinearOpMode() {

    override fun runOpMode() {

        waitForStart()

        constants.angles.forEach {angleRange->
            val algorithm = PSO_Optimizer(arrayListOf(
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy/3.5),
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy)
            ),sim,Time,angleRange,constants.obstacle)

            algorithm.update(25)
            telemetry.addLine(algorithm.getBest().toString())

            telemetry.update()

        }

        stop()
    }
}