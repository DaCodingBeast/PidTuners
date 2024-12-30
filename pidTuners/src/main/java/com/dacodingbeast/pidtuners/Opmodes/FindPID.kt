package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous
class FindPID : LinearOpMode() {

    override fun runOpMode() {

        val algorithm = PSO_Optimizer()


        algorithm.update(25)
        telemetry.addLine(algorithm.getBest().toString())
        telemetry.update()

        stop()
    }
}