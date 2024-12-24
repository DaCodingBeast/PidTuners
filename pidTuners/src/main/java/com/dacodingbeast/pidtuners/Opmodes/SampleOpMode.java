package com.dacodingbeast.pidtuners.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.App.Constant;

import java.util.ArrayList;

import CommonUtilities.AngleRange;
@Config
@TeleOp(name = "SampleOpMode", group = "Linear OpMode")
public class SampleOpMode extends LinearOpMode {

    public static int x = 0;
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);

        Constants constants = new Constants();

        // Code executed when initialized
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, constants.motorName);
        motor.setDirection(constants.motorDirection);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0.0);

        ArrayList<AngleRange> targets = Constant.angleRanges;
        AngleRange target = targets.get(0);
        ElapsedTime timer = new ElapsedTime();

        constants.pidfController.resetConstantsAndTarget(Constant.params.get(0), target);
        x=0;
        ElapsedTime timerTime = new ElapsedTime();
        while (opModeInInit()){
            timerTime.reset();
            timer.reset();
        }
        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            double looptime = timer.seconds();
            timer.reset();

            int encoder = (int) ((motor.getCurrentPosition())/ Math.pow(constants.motor.getCustomGearRatio(),2));

            if (constants.pidfController.targetReached(encoder,8)){
                if(targets.size()> x+1 && timerTime.seconds() >= 1.0) {
                    x+=1;
                    target = targets.get(x);
                    timerTime.reset();
                }
            }

            for (int i = 0; i < targets.size(); i++) {
                if (target == targets.get(i)) {
                    constants.pidfController.resetConstantsAndTarget(Constant.params.get(i), target);
                    break;
                }
            }

            motor.setPower(constants.pidfController.calculateMotorPower(encoder, looptime));
            telemetry.addData("X",x);
            telemetry.update();
        }
    }
}