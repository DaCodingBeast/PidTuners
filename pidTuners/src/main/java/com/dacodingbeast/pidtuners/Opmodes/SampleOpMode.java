package com.dacodingbeast.pidtuners.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.dacodingbeast.pidtuners.Simulators.Target;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

//@Config
//@TeleOp(name = "SampleOpMode", group = "Linear OpMode")
public class SampleOpMode extends LinearOpMode {
    public static int x = 0;
    Motors motor;

    public SampleOpMode(Motors motor) {
        this.motor = motor;
    }
    @Override
    public void runOpMode() {
        DataLogger.getInstance().startLogger("SampleOpMode" + motor.getName());
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);

        motor.init(hardwareMap,0.0);
        x = 0;
        ElapsedTime timerTime = new ElapsedTime();

        while (opModeInInit()) {
            timerTime.reset();
        }

        waitForStart();


        //todo cases for both motors (accuracy needs to be different)

        while (opModeIsActive() && !isStopRequested()) {

            List<Target> targets = motor.getTargets();

            Target target = motor.getTargets().get(x);


            if (motor.targetReached(target.getStop())) {
                telemetry.addData("Target Reached: ", true);
                if (targets.size() > x + 1 && timerTime.seconds() >= 1.0) {
                    x += 1;
                    timerTime.reset();
                }
            }
            else{
                telemetry.addData("Target Reached: ", false);
            }

            motor.run(x);

            telemetry.update();
        }
    }
}