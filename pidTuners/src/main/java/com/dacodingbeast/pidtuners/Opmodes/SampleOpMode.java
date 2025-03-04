package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.stationaryAngle;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.dacodingbeast.pidtuners.Simulators.Target;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

import CommonUtilities.PIDFcontroller;

@Config
@TeleOp(name = "SampleOpMode", group = "Linear OpMode")
public class SampleOpMode extends LinearOpMode {
    public static int x = 0;
    Motors motor;

    public SampleOpMode(Motors motor) {
        this.motor = motor;
    }

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);

        motor.init(hardwareMap, stationaryAngle);

        ElapsedTime timer = new ElapsedTime();

        x = 0;


        ElapsedTime timerTime = new ElapsedTime();
        while (opModeInInit()) {
            timerTime.reset();
            timer.reset();
        }
        waitForStart();
        //todo cases for both motors (accuracy needs to be different)

        while (opModeIsActive() && !isStopRequested()) {
            double looptime = timer.seconds();
            timer.reset();
            PIDFcontroller pidFcontroller = motor.getPIDFController();
            List<Target> targets = motor.getTargets();
            Target target = motor.getTargets().get(x);
            if (motor.targetReached(target.getStop(), null)) {
                if (targets.size() > x + 1 && timerTime.seconds() >= 1.0) {
                    x += 1;
                    target = targets.get(x);
                    timerTime.reset();
                }
            }

            motor.run(x);
            telemetry.addData("X", x);
            telemetry.update();
        }
    }
}