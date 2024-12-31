package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.gravityDisplayPoints;
import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.gravityMotorPower;
import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.gravityRecord;
import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.stationaryAngle;

import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.Constants.Constants;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.Arrays;


@TeleOp(name = "GravityTest", group = "Linear OpMode")
public class GravityTest extends LinearOpMode {
    Constants constants;
    public GravityTest(Constants constants) {
        this.constants = constants;
    }
    @Override
    public void runOpMode() {
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);;

        Motor motor = constants.getMotor();
        motor.init(hardwareMap,stationaryAngle);

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        while (opModeIsActive()) {

            double angle = motor.findAngle(false);
            //todo double angle = get voltage and convert to Radians if using an absolute encoder

            telemetry.addLine("Press Record to store data points, and display data points when done.");

            motor.setPower(gravityMotorPower);

            if (gravityRecord) {
                dataPairs.add(new Pair<>(
                        angle,
                        motor.calculateTmotor(
                                motor.getPower(),
                                constants.getSystemSpecific().getFrictionRPM()
                        )
                ));
                gravityRecord = false;
            }

            if (gravityDisplayPoints) {
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