package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class FindPID(val motor: Motors, private val accuracy:Double, private val Time:Double) : LinearOpMode() {

    override fun runOpMode() {

        waitForStart()



        for(i in motor.targets.indices){

            val algorithm = PSO_Optimizer(arrayListOf(
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy/3.5),
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy)
            ),Time,motor,i)

            algorithm.update(25)
            telemetry.addLine(algorithm.getBest().toString())

            telemetry.update()

        }

        stop()
    }
}