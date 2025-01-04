package com.dacodingbeast.pidtuners.Opmodes;


import com.dacodingbeast.pidtuners.Constants.GravityModelConstants;
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants;
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.HardwareSetup.Motors;
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor;
import com.dacodingbeast.pidtuners.Simulators.AngleRange;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import CommonUtilities.PIDParams;

public final class TuningOpModes {
    static AngleRange obstacleAngle = AngleRange.Angles.fromDegrees(0, 90);
    static AngleRange testingAngle = AngleRange.Angles.fromDegrees(90, 180);

    static double stationaryAngle = Math.toRadians(0.0);
    static double frictionRPM = 0.0;
    static PIDParams pidParams = new PIDParams(0.0,0.0,0.0,0.0);
    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(0.0,frictionRPM, new  GravityModelConstants(0.0,0.0,0.0));
    public static ArmMotor armMotor = new ArmMotor("Shoulder",DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,pivotSystemConstants,1.0,pidParams,testingAngle.asArrayList(),null,obstacleAngle);
    public static Double spoolDiameter = 1.0;

    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0,frictionRPM);

    public static SlideMotor slideMotor = new SlideMotor("Slide",DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,slideSystemConstants,1.0,pidParams,testingAngle.asArrayList(),null,obstacleAngle);

    static double accuracy = 3.5;

    static double time = 30.0;

    static double gravityMotorPower = 0.5;

    static boolean enableSlides = true;
    static boolean enableArm = true;
    private TuningOpModes() {
    }

    private static OpModeMeta metaForClass(Class<? extends OpMode> cls, Motors motors) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName() + (motors.getMotorType()))
                .setGroup("PIDTuners")
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        if (enableArm) {
            manager.register(
                    metaForClass(FrictionTest.class, armMotor), new FrictionTest(armMotor)
            );
            manager.register(
                    metaForClass(GravityTest.class, armMotor), new GravityTest(armMotor)
            );
            manager.register(
                    metaForClass(SampleOpMode.class, armMotor), new SampleOpMode(armMotor)
            );
            manager.register(
                    metaForClass(FindPID.class, armMotor), new FindPID(armMotor,accuracy,time)
            );
        }
        if (enableSlides) {
            manager.register(
                    metaForClass(FrictionTest.class, slideMotor), new FrictionTest(slideMotor)
            );
            manager.register(
                    metaForClass(SampleOpMode.class, slideMotor), new SampleOpMode(slideMotor)
            );
            manager.register(
                    metaForClass(FindPID.class, slideMotor), new FindPID(slideMotor,accuracy,time)
            );
        }
    }

}
