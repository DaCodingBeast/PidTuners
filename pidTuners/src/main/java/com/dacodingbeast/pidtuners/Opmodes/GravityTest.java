package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.gravityMotorPower;
import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.stationaryAngle;

import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.Algorithm.QuadraticRegression;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.Arrays;


@TeleOp(name = "GravityTest", group = "Linear OpMode")
public class GravityTest extends LinearOpMode {
    ArmMotor motor;
    public GravityTest(ArmMotor motor) {
        this.motor = motor;
    }
    @Override
    public void runOpMode() {
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);;

        motor.init(hardwareMap,stationaryAngle);

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        while (opModeIsActive()) {

            double angle = motor.findPosition(false);
            //todo double angle = get voltage and convert to Radians if using an absolute encoder

            telemetry.addLine("Press Record to store data points, and display data points when done.");

            motor.setPower(gravityMotorPower);

                dataPairs.add(new Pair<>(
                        angle,
                        motor.calculateTmotor(
                                motor.getPower(),
                                motor.getSystemConstants().getFrictionRPM()
                        )
                ));

                for (Pair<Double, Double> dataPoint : dataPairs) {
                    double [] d = new double[]{dataPoint.first,dataPoint.second};
                    telemetry.addLine(Arrays.toString(d));
                }
            double[] x = new double[dataPairs.size()];
            double[] y = new double[dataPairs.size()];
            for (int i = 0; i < dataPairs.size(); i++) {
                x[i] = dataPairs.get(i).first;
                y[i] = dataPairs.get(i).second;
            }
            double[] coefficients = QuadraticRegression.quadraticRegressionManual(x, y);
            double intercept = coefficients[0];
            double linear = coefficients[1];
            double quadratic = coefficients[2];
            telemetry.addLine("Place this in your gravity constants in TuningOpModes");
            telemetry.addData("c", intercept);
            telemetry.addData("b", linear);
            telemetry.addData("a", quadratic);

//                telemetry.addLine("Input data points into a table in https://www.desmos.com/calculator");
//                telemetry.addLine("Copy and paste the below equation, and place a,b,k in the config");
//                telemetry.addLine("y_{1}~a(x_{1}-b)^2+k");
//                telemetry.addLine("All done!!");

            telemetry.update();
        }
    }
}