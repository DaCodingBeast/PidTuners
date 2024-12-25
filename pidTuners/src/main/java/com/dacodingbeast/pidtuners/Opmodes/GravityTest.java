package com.dacodingbeast.pidtuners.Opmodes;

import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.CommonUtilities.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.Arrays;

import CommonUtilities.Models;

@TeleOp(name = "GravityTest", group = "Linear OpMode")
public class GravityTest extends LinearOpMode {
    com.dacodingbeast.pidtuners.CommonUtilities.PivotConstants constants;
    public GravityTest(com.dacodingbeast.pidtuners.CommonUtilities.PivotConstants constants) {
        this.constants = constants;
    }
    @Override
    public void runOpMode() {
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);

        constants.getMotor().setup(hardwareMap);
        Hardware.Motor motor = constants.getMotor();

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        while (opModeIsActive()) {

            double angle = constants.getArmAngle().findAngle((int) ((motor.getCurrentPose())/ Math.pow(constants.getMotor().getSpecs().getCustomGearRatio(),2)));
            //todo double angle = get voltage and convert to Radians if using an absolute encoder

            telemetry.addLine("Press Record to store data points, and display data points when done.");

            motor.setPower(Constants.gravityMotorPower);

            if (Constants.gravityRecord) {
                dataPairs.add(new Pair<>(
                        angle,
                        Models.calculateTmotor(
                                motor.getPower(),
                                constants.getMotor(),
                                constants.frictionRPM //TODO
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

