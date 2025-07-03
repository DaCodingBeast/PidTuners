package com.dacodingbeast.pidtuners.Opmodes;


import android.util.Pair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit;
import com.dacodingbeast.pidtuners.Simulators.AngleRange;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.dacodingbeast.pidtuners.utilities.MathFunctions.QuadraticRegression;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.Arrays;

public class GravityTest extends LinearOpMode {
    ArmMotor motor;
    AngleRange angleRange;

    public GravityTest(ArmMotor motor, AngleRange angleRange) {
        this.motor = motor;
        this.angleRange = angleRange;
    }


    @Override
    public void runOpMode() {
        DataLogger.getInstance().startLogger("GravityTest" + motor.getName());
        if (motor.getClass() != ArmMotor.class) throw new RuntimeException("Motor must be an arm motor");
        if (PowerConstants.getGravityMotorPower() == 0) throw new RuntimeException("Gravity motor power cannot be 0");
        MultipleTelemetry telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), this.telemetry);

        telemetry.addLine("Press Record to store data points, and display data points when done.");
        telemetry.addLine("Data will be output to logcat under: 'tag:pidtunersdatalogger'");
        telemetry.update();
        motor.init(hardwareMap, 0.0);

        ArrayList<Pair<Double, Double>> dataPairs = new ArrayList<>();

        waitForStart();
        double target = 0;
        boolean targetHit = false;
        boolean run;
        DataLogger.getInstance().logDebug("target: "+target);
        while (opModeIsActive()) {

            double angle = motor.findPosition();

            target = (angleRange.getStop());
            run = !motor.targetReached(target);

            if (run) {
                motor.setPower(PowerConstants.getGravityMotorPower());
            }else{
                motor.setPower(0);
                targetHit = true;
            }

            dataPairs.add(new Pair<>(
                    angle,
                    motor.calculateTmotor(
                            motor.getPower(),
                            motor.getSystemConstants().getFrictionRPM(),
                            TorqueUnit.KILOGRAM_CENTIMETER
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
            if (x.length>3) { // absolute minimum is 3
                double[] coefficients = QuadraticRegression.quadraticRegressionManual(x, y);
                double intercept = coefficients[0];
                double linear = coefficients[1];
                double quadratic = coefficients[2];
                double[] vertex = QuadraticRegression.toVertexForm(quadratic, linear, intercept);

                telemetry.addLine("Place this in your gravity constants in TuningOpModes");
                telemetry.addData("a", vertex[0]);
                telemetry.addData("h", vertex[1]);
                telemetry.addData("k", vertex[2]);
                DataLogger.getInstance().logDebug("Data: " + dataPairs);

                DataLogger.getInstance().logDebug("a: " + vertex[0] + " h: " + vertex[1] + " k: " + vertex[2]);
//                telemetry.addLine("Input data points into a table in https://www.desmos.com/calculator");
//                telemetry.addLine("Copy and paste the below equation, and place a,b,k in the config");
//                telemetry.addLine("y_{1}~a(x_{1}-b)^2+k");
//                telemetry.addLine("All done!!");
            }
            if (targetHit){
                requestOpModeStop();
            }
            telemetry.update();
        }
    }
}