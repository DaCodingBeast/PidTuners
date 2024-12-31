package com.dacodingbeast.pidtuners.Opmodes;

import static com.dacodingbeast.pidtuners.Opmodes.TuningOpModes.stationaryAngle;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.dacodingbeast.pidtuners.Constants.Constants;
import com.dacodingbeast.pidtuners.Constants.PivotConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.dacodingbeast.pidtuners.Simulators.Target;
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

import CommonUtilities.PIDFcontroller;

@Config
@TeleOp(name = "PivotSampleOpMode", group = "Linear OpMode")
public class SampleOpMode extends LinearOpMode {
    Constants constants;
    PIDFcontroller pidFcontroller;
    public static int x = 0;
    public SampleOpMode(Constants constants, PIDFcontroller pidFcontroller) {
        this.constants = constants;
        this.pidFcontroller = pidFcontroller;
    }
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);

        Motor motor = constants.getMotor();
        motor.init(hardwareMap,stationaryAngle);

        List<Target> targets = constants.getAngles();
        ElapsedTime timer = new ElapsedTime();

        x=0;


        ElapsedTime timerTime = new ElapsedTime();
        while (opModeInInit()){
            timerTime.reset();
            timer.reset();
        }
        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            double looptime = timer.seconds();
            timer.reset();

            int encoder = (int) ((motor.getCurrentPose())/ Math.pow(constants.getMotor().getGearRatio(),2));

            if (pidFcontroller.targetReached(encoder,8)){
                if(targets.size()> x+1 && timerTime.seconds() >= 1.0) {
                    x+=1;
                    target = targets.get(x);
                    timerTime.reset();
                }
            }

            for (int i = 0; i < targets.size(); i++) {
                if (target == targets.get(i)) {
                    pidFcontroller.resetConstantsAndTarget(pidFcontroller.getParams(), target);
                    break;
                }
            }

            motor.setPower(pidFcontroller.calculateMotorPower(encoder, looptime));
            telemetry.addData("X",x);
            telemetry.update();
        }
    }
}