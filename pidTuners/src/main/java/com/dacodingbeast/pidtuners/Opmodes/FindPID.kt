package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.CommonUtilities.PivotConstants
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous
class FindPID(val constants:PivotConstants,val accuracy:Double,val sim:SimulatorType,val time:Double) : LinearOpMode() {


    override fun runOpMode() {

        val algorithm = PSO_Optimizer(arrayListOf(
            Ranges(0.0, accuracy),
            Ranges(0.0, accuracy/3.5),
            Ranges(0.0, accuracy),
            Ranges(0.0, accuracy)
        ),sim,time,constants.testingAngle,constants.obstacle)

        waitForStart()

        algorithm.update(25)
        telemetry.addLine(algorithm.getBest().toString())
        telemetry.update()

        stop()
    }
}