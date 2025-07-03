package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.utilities.MathFunctions.RemoveOutliersKt.removeOutliers;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor;
import com.dacodingbeast.pidtuners.Simulators.SlideRange;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public class SlidesTest extends LinearOpMode {
    SlideMotor motor;
    SlideRange slideRange;
    ArrayList<Double> RPM_history = new ArrayList<>();
    ArrayList<Double> linearAccelerations = new ArrayList<>();
    ArrayList<Double> motorPowers = new ArrayList<>();
    double motorPowerConstant = 0.5;
    double accurateRPM_Constant;


    public SlidesTest(Motors motor, int index) {
        this.motor = (SlideMotor) motor;
        this.slideRange = this.motor.getTargets().get(index);
    }

    public SlidesTest(Motors motor) {
        this.motor = (SlideMotor) motor;
        this.slideRange = this.motor.getTargets().get(0);
    }

    public void solveForConstants() {
        ArrayList<Double> cleansedLinearAccel_history = removeOutliers(linearAccelerations);

        //only if we want to test at different speeds to improve accuracy
        ArrayList<Double> cleansedMoto = removeOutliers(motorPowers);

        double sum = 0;
        for (double num : cleansedLinearAccel_history) sum += num;

        double averageLinearAccel = sum / cleansedLinearAccel_history.size();
        double motorTorque = motor.calculateTmotor(motorPowerConstant, accurateRPM_Constant);

        double SlidesMass = (motorTorque / motor.getSpoolDiameter()) / averageLinearAccel;
        if (accurateRPM_Constant == 0 && SlidesMass == 0) {
            DataLogger.getInstance().logError("No data found, please run the test again");
        } else {
            DataLogger.getInstance().logDebug("frictionRPM: " + accurateRPM_Constant);
            DataLogger.getInstance().logData("effectiveMass: " + SlidesMass);
        }
        requestOpModeStop();
    }

    public void updateRPM(double newRPM) {
        RPM_history.add(newRPM);

        if (RPM_history.size() >= 10) {
            ArrayList<Double> cleansedData = removeOutliers(RPM_history);

            double sum = 0;
            for (double num : cleansedData) sum += num * 1 / motorPowerConstant;

            double actualRpm = sum / cleansedData.size();
            accurateRPM_Constant = actualRpm;
            telemetry.addData("Motor RPM", actualRpm);
            DataLogger.getInstance().logDebug("actualRpm: " + actualRpm);
        }
    }

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);
        DataLogger.getInstance().startLogger("SlidesTest" + motor.getName());
        telemetry.addLine("Please rotate your robot so that gravity does not affect your mechanism");
        telemetry.addLine("Data will be output to logcat under: 'tag:pidtunersdatalogger'");
        telemetry.addLine();
        telemetry.addData("instantaneous velocity", 0.0);
        telemetry.addData("instantaneous acceleration", 0.0);
        telemetry.addData("interval acceleration", 0.0);
        telemetry.addData("interval velocity", 0.0);
        motor.init(hardwareMap, 0.0);
        telemetry.update();


        ElapsedTime timer = new ElapsedTime();
        ElapsedTime velocityTimer = new ElapsedTime();
        ElapsedTime accelerationTimer = new ElapsedTime();

        double ticksPerRevolution = motor.getTicksPerRotation();
        double theoreticalRpmMeasured = motor.getRPM() * .5;

        double StartExtension = motor.findPosition();
        boolean reachedTarget;
        double target;
        int lastEncoderPosition = motor.motor.getCurrentPosition();
        double lastExtension = StartExtension;
        double lastInstantenousVelocity = 0.0;


        double intervalVelocityLastExtension = StartExtension;
        double intervalAccelerationLastVelocity = 0.0;
//        ArrayList<Double> intervalVelocities = new ArrayList<>();

        int loopsToCountRPM = 4;
        int loopsToCountVelo = 5;
        int loopsToCountAccel = 5 * loopsToCountVelo;
        int loopCount = 0;


        waitForStart();

        while (opModeIsActive()) {

            if (loopCount == 0) {
                telemetry.addData("instantaneous velocity", 0.0);
                telemetry.addData("instantaneous acceleration", 0.0);
                telemetry.addData("interval acceleration", 0.0);
                telemetry.addData("interval velocity", 0.0);

                timer.reset();
                velocityTimer.reset();
                accelerationTimer.reset();
            }


            target = slideRange.getStop();// inches
//            DataLogger.getInstance().logDebug("target: " + target);

            reachedTarget = motor.targetReached(target);

            double extension = motor.findPosition();
            telemetry.addData("Position", extension);

            double deltaT = timer.seconds();
            double rpm = ((motor.getCurrentPose() - lastEncoderPosition) / ticksPerRevolution) * (60.0 / deltaT);

            double instantaneousVelocity = (extension - lastExtension) / deltaT;
            double instantaneousAcceleration = (instantaneousVelocity - lastInstantenousVelocity) / deltaT;

            telemetry.addData("instantaneous velocity", instantaneousVelocity);
            telemetry.addData("instantaneous acceleration", instantaneousAcceleration);


            if (!reachedTarget) {
                motor.setPower(motorPowerConstant);

                boolean validWindow = (rpm > theoreticalRpmMeasured * .5 && rpm < theoreticalRpmMeasured * 1.5)
                        && extension > lastExtension && extension - StartExtension >= 2;

                if (validWindow) {
                    motorPowers.add(motorPowerConstant);

                    if (loopCount % loopsToCountRPM == 0) {
                        updateRPM(rpm);
                    }


                    if (loopCount % loopsToCountVelo == 0) {
                        double dtV = velocityTimer.seconds();
                        double velocity = (extension - intervalVelocityLastExtension) / dtV;
//                        intervalVelocities.add(velocity);
                        intervalVelocityLastExtension = extension;
                        velocityTimer.reset();


                        telemetry.addData("interval velocity", velocity);

                        if (loopCount % loopsToCountAccel == 0) {
                            double dtA = accelerationTimer.seconds();
                            double acceleration = (velocity - intervalAccelerationLastVelocity) / dtA;

                            linearAccelerations.add(acceleration);
                            intervalAccelerationLastVelocity = velocity;
                            accelerationTimer.reset();


                            telemetry.addData("interval acceleration", acceleration);
                        }


                    }

                }

            } else {
                motor.setPower(0);
                solveForConstants();
            }


            lastExtension = extension;
            lastInstantenousVelocity = instantaneousVelocity;
            lastEncoderPosition = (int) motor.getCurrentPose();
            timer.reset();
            loopCount += 1;
            telemetry.update();
        }
    }
}
