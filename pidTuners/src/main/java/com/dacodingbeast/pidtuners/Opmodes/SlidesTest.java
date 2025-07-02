package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.utilities.MathFunctions.RemoveOutliersKt.removeOutliers;

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


    public SlidesTest(Motors motor, SlideRange slideRange) {
        this.motor = (SlideMotor) motor;
        this.slideRange = slideRange;
    }


    public void solveForConstants(){
        ArrayList<Double> cleansedLinearAccel_history = removeOutliers(linearAccelerations);

        //only if we want to test at different speeds to improve accuracy
        ArrayList<Double> cleansedMoto = removeOutliers(motorPowers);

        double sum = 0;
        for (double num : cleansedLinearAccel_history) sum += num;

        double averageLinearAccel = sum / cleansedLinearAccel_history.size();
        double motorTorque = motor.calculateTmotor(motorPowerConstant, accurateRPM_Constant);

        double SlidesMass = (motorTorque/motor.getSpoolDiameter())/averageLinearAccel;

        DataLogger.getInstance().logDebug("frictionRPM: " + accurateRPM_Constant);
        DataLogger.getInstance().logData("effectiveMass: "+ SlidesMass);
        requestOpModeStop();
    }

    public void updateRPM(double newRPM){
        RPM_history.add(newRPM);

        if (RPM_history.size() >=10){
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
        DataLogger.getInstance().startLogger("SlidesTest" + motor.getName());
        telemetry.addLine("Please rotate your robot so that gravity does not affect your mechanism");
        telemetry.addLine("Data will be output to logcat under: 'tag:pidtunersdatalogger'");
        motor.init(hardwareMap, 0.0);
        telemetry.update();


        ElapsedTime timer = new ElapsedTime();

        double ticksPerRevolution = motor.getTicksPerRotation();
        double theoreticalRpmMeasured = motor.getRPM() * .5;

        boolean reachedTarget;
        double target;
        int lastEncoderPosition=0;
        double lastExtension =0.0;
        double lastVelocity = 0.0;


        waitForStart();

        if (!opModeInInit()) {
            timer.reset();
        }

        while (opModeIsActive()) {
            target = slideRange.getStop();// inches

            reachedTarget = motor.targetReached(target*motor.getConversions().getTicksPerInch());

            double extension = motor.findPosition();
            telemetry.addData("Position", extension);


            double rpm = ((motor.getCurrentPose() - lastEncoderPosition) / ticksPerRevolution) * (60.0 / timer.seconds());


            double linearVelocity = (extension-lastExtension)/timer.seconds();
            double linearAcceleration = (linearVelocity - lastVelocity)/timer.seconds();

            if (!reachedTarget) {
                motor.setPower(motorPowerConstant);

                boolean validWindow = (rpm > theoreticalRpmMeasured * .5 && rpm < theoreticalRpmMeasured * 1.5)
                        && extension > lastExtension;

                if (validWindow){
                    linearAccelerations.add(linearAcceleration);
                    motorPowers.add(motor.getPower());
                    updateRPM(rpm);
                }

            }else{
                motor.setPower(0);
                solveForConstants();
            }


            lastExtension = extension;
            lastVelocity = linearVelocity;
            lastEncoderPosition = (int) motor.getCurrentPose();
            timer.reset();
            telemetry.update();
        }
    }
}
