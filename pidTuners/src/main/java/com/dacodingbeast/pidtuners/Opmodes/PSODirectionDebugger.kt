package com.dacodingbeast.pidtuners.Opmodes

import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

class PSODirectionDebugger(val slideMotor: SlideMotor,val armMotor: ArmMotor):LinearOpMode() {
    override fun runOpMode() {
        slideMotor.init(hardwareMap,0.0)
        armMotor.init(hardwareMap,0.0)
        waitForStart()
        while(opModeIsActive()){

            telemetry.addLine("Press square for slide +")
            telemetry.addLine("Press circle for slide -")
            telemetry.addLine("Press triangle for arm +")
            telemetry.addLine("Press cross for arm -")
            if (gamepad1.square){
                slideMotor.setPower(1.0)
            }else if (gamepad1.circle){
                slideMotor.setPower(-1.0)
            }else{
                slideMotor.setPower(0.0)
            }
            if (gamepad1.triangle){
                armMotor.setPower(1.0)
            }else if (gamepad1.cross){
                armMotor.setPower(-1.0)
            }
            telemetry.addData("Slide Motor pose (in)",slideMotor.getExtension())
            telemetry.addData("Arm Motor pose (ticks)",armMotor.getCurrentPose())
            telemetry.update()
        }
    }
}