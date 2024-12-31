package com.dacodingbeast.pidtuners.Opmodes;

import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Constants;

import java.util.ArrayList;
import java.util.Arrays;

import ArmSpecific.ArmAngle;
import CommonUtilities.Models;

@TeleOp(name = "GravityTest", group = "Linear OpMode")
public class GravityTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);

        Constants constants = new Constants();

        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, constants.motorName);

        motor.setDirection(constants.motorDirection);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0.0);

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        while (opModeIsActive()) {

            double angle = constants.armAngle.findAngle(motor.getCurrentPosition());
            //todo double angle = get voltage and convert to Radians if using an absolute encoder

            telemetry.addLine("Press Record to store data points, and display data points when done.");

            motor.setPower(Constants.gravityMotorPower);

            if (Constants.gravityRecord) {
                dataPairs.add(new Pair<>(
                        angle,
                        Models.calculateTmotor(
                                motor.getPower(),
                                constants.motor,
                                Constants.frictionRPM
                        )
                ));
                Constants.gravityRecord = false;
            }

            if (Constants.gravityDisplayDataPoints) {
                for (Pair<Double, Double> dataPoint : dataPairs) {
                    double [] d = new double[]{dataPoint.first,dataPoint.second};
                    telemetry.addLine(Arrays.toString(d));
                }
                telemetry.addLine("Input data points into a table in https://www.desmos.com/calculator");
                telemetry.addLine("Copy and paste the below equation, and place a,b,k in the config");
                telemetry.addLine("y_{1}~a(x_{1}-b)^2+k");
                telemetry.addLine("All done!!");
            }
            telemetry.update();
        }
    }
}