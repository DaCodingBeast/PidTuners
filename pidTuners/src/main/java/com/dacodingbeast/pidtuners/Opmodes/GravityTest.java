package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TestingSize.gravityMotorPower;

import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.dacodingbeast.pidtuners.utilities.MathFunctions.QuadraticRegression;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.Arrays;


//@TeleOp(name = "GravityTest", group = "Linear OpMode")
public class GravityTest extends LinearOpMode {
    ArmMotor motor;

    public GravityTest(ArmMotor motor) {
        this.motor = motor;
    }

    @Override
    public void runOpMode() {
        DataLogger.getInstance().startLogger("GravityTest" + motor.getName());
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);

        motor.init(hardwareMap, TestingSize.start);

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        while (opModeIsActive()) {

            double angle = motor.findPosition(false);

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
                double[] d = new double[]{dataPoint.first, dataPoint.second};
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
            double[] vertex = QuadraticRegression.toVertexForm(quadratic, linear, intercept);
            telemetry.addLine("Place this in your gravity constants in TuningOpModes");
            telemetry.addData("a", vertex[0]);
            telemetry.addData("h", vertex[1]);
            telemetry.addData("k", vertex[2]);

            DataLogger.getInstance().logDebug("a: " + vertex[0] + " h: " + vertex[1] + " k: " + vertex[2]);

//                telemetry.addLine("Input data points into a table in https://www.desmos.com/calculator");
//                telemetry.addLine("Copy and paste the below equation, and place a,b,k in the config");
//                telemetry.addLine("y_{1}~a(x_{1}-b)^2+k");
//                telemetry.addLine("All done!!");

            telemetry.update();
        }
    }
}