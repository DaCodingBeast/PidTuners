package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.utilities.MathFunctions.RemoveOutliersKt.removeOutliers;
import static java.lang.Math.abs;

import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.dacodingbeast.pidtuners.HardwareSetup.torque.TorqueUnit;
import com.dacodingbeast.pidtuners.Simulators.AngleRange;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public class FrictionTest extends LinearOpMode {
    Motors motor;
    AngleRange angleRange;

    public FrictionTest(Motors motor, AngleRange angleRange) {
        if (motor.getClass() != ArmMotor.class) throw new IllegalArgumentException("Motor must be an ArmMotor");
        this.motor = motor;
        this.angleRange = angleRange;
    }

    @Override
    public void runOpMode() {
        DataLogger.getInstance().startLogger("FrictionTest" + motor.getName());
        telemetry.addLine("Please rotate your robot so that gravity does not affect your mechanism");
        telemetry.addLine("Data will be output to logcat under: 'tag:pidtunersdatalogger'");
        motor.init(hardwareMap, 0.0);

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
        boolean run;
        telemetry.update();
        waitForStart();
        if (!opModeInInit()) {
            timer.reset();
        }
        double target;
        boolean targetHit = false;
        if(motor.getClass() == ArmMotor.class){
            DataLogger.getInstance().logDebug("target: "+angleRange.getStop());
        }
        while (opModeIsActive()) {
            target = (angleRange.getStop()); // should be in radians
            run = !motor.targetReached(target); // does calcs in rads

            double position=motor.findPosition();

            if (run) {
                motor.setPower(0.5);
                telemetry.addData("Running", motor.getRPM() * .5);
            }else{
                motor.setPower(0);
                targetHit = true;
            }
            telemetry.addData("Position", position);

            // Measure RPM
            double ticksPerRevolution = motor.getTicksPerRotation(); // Encoder resolution (ticks per revolution)
            double rpm = ((motor.getCurrentPose() - lastPosition) / ticksPerRevolution) * (60.0 / timer.seconds());
            lastPosition = (int) motor.getCurrentPose();

            telemetry.addData("rpm", rpm);

            double theoreticalRpmMeasured = motor.getRPM() * .5;
            if (run && (rpm > theoreticalRpmMeasured * .5 && rpm < theoreticalRpmMeasured * 1.5) && position > lastAngle) {
                RPMS.add(rpm);
            }
            telemetry.addData("t", theoreticalRpmMeasured);
//            telemetry.addData("pose", motor.getCurrentPose());
            telemetry.addData("target",target);


            // Make sure size is not returning something other than 0
            if (!RPMS.isEmpty()) {
                ArrayList<Double> x = removeOutliers(RPMS);
                double sum = 0;
                for (double num : x) sum += num * 1 / .5;
                actualRpm = sum / x.size();
                telemetry.addData("Motor RPM", actualRpm);
                DataLogger.getInstance().logDebug("actualRpm: " + actualRpm);
            }

            // Finding Angular Acceleration
            angularVelocity = (position - lastAngle) / timer.seconds();
            angularAccel = abs((angularVelocity - lastVelocity) / timer.seconds());


            if (run) {
                angularAccelerationData.add(angularAccel);
                motorPowers.add(motor.getPower());
            }
            if (targetHit){
                // Calculate if friction test is complete and find rotational Inertia

                angularAccelerationData = removeOutliers(angularAccelerationData);
                motorPowers = removeOutliers(motorPowers);

                double sum = 0;
                for (double num : angularAccelerationData) {
                    sum += num;
                }
                double averageAA = sum / angularAccelerationData.size();

                double rotationalInertia = motor.calculateTmotor(
                        .5,
                        actualRpm, TorqueUnit.KILOGRAM_CENTIMETER
                ) / averageAA;

                telemetry.addData("Inertia", rotationalInertia);
                DataLogger.getInstance().logDebug("frictionRPM: " + actualRpm);
                DataLogger.getInstance().logData("rotationalInertia: "+rotationalInertia);
                requestOpModeStop();
            }

            lastAngle = position;
            lastVelocity = angularVelocity;
            timer.reset();
            telemetry.update();
        }
    }
}
