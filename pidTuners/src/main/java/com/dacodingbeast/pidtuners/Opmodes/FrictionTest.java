package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.stationaryAngle;
import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

@TeleOp(name = "FrictionTest", group = "Linear OpMode")
public class FrictionTest extends LinearOpMode {
    Motors motor;
    public FrictionTest(Motors motor) {
        this.motor = motor;
    }

    RemoveOutliers removeOutliers = new RemoveOutliers();
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);

        motor.init(hardwareMap,stationaryAngle);

        ElapsedTime timer = new ElapsedTime();
        ArrayList<Double> RPMS = new ArrayList<>();
        double angularAccel;
        int lastPosition = 0;
        double angularVelocity;
        double lastAngle = 0.0;
        double lastVelocity = 0.0;
        ArrayList<Double> angularAccelerationData = new ArrayList<>();
        ArrayList<Double> motorPowers = new ArrayList<>();
        double actualRpm = 0.0;
        boolean run  = true;

        waitForStart();
        if (!opModeInInit()) {
            timer.reset();
        }
        while (opModeIsActive()) {

            telemetry.addLine("Please rotate your robot so that gravity does not affect your mechanism");

            // Running motor at half speed
            double angle = motor.findPosition(); //TODO this needs to also be able to use slides

            //todo double angle = get voltage and convert to Radians if using an absolute encoder

            if(run) {
                motor.setPower(0.5);
                telemetry.addData("Running", motor.getRPM()*.5);
                telemetry.addData("Angle", angle);
            };

            // Measure RPM
            double ticksPerRevolution = motor.getTicksPerRotation(); // Encoder resolution (ticks per revolution)
            double rpm = ((motor.getCurrentPose() - lastPosition) / ticksPerRevolution) * (60.0 / timer.seconds());
            lastPosition = (int) motor.getCurrentPose();

            telemetry.addData("rpm",rpm);

            double theoreticalRpmMeasured = motor.getRPM() * .5;
            if (run && (rpm > theoreticalRpmMeasured*.5 && rpm<theoreticalRpmMeasured *1.5) && angle > (motor.getTargets().get(0).getStop()*.5)) {
                RPMS.add(rpm);
            }
            telemetry.addData("t",theoreticalRpmMeasured);

//            else telemetry.addLine("Rpm Constants is incorrect, or your robot is struggling with the amount of weight it has");

            // Make sure size is not returning something other than 0
            if (!RPMS.isEmpty()) {
                ArrayList<Double> x = removeOutliers.removeOutliers(RPMS);
                double sum = 0;
                for (double num : x) sum += num * 1/.5;
                actualRpm = sum / x.size();
                telemetry.addData("Motor RPM", actualRpm);
            }

            // Finding Angular Acceleration
            angularVelocity = (angle - lastAngle) / timer.seconds();
            angularAccel = abs((angularVelocity - lastVelocity) / timer.seconds());



            if (run) {
                angularAccelerationData.add(angularAccel);
                motorPowers.add(motor.getPower());
            } else {
                // Calculate if friction test is complete and find rotational Inertia

                angularAccelerationData = removeOutliers.removeOutliers(angularAccelerationData);
                motorPowers = removeOutliers.removeOutliers(motorPowers);

                double sum = 0;
                for (double num : angularAccelerationData) {
                    sum += num;
                }
                double averageAA = sum / angularAccelerationData.size();

                double rotationalInertia = motor.calculateTmotor(
                        .5,
                        actualRpm
                ) / averageAA;

                telemetry.addData("Inertia",rotationalInertia);
                stop();
            }

            lastAngle = angle;
            lastVelocity = angularVelocity;
            timer.reset();
            telemetry.update();
        }
    }
}
