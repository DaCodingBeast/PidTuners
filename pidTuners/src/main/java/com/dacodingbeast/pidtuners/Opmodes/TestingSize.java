package com.dacodingbeast.pidtuners.Opmodes;


import com.dacodingbeast.pidtuners.Constants.GravityModelConstants;
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.Simulators.AngleRange;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import CommonUtilities.PIDParams;

public final class TestingSize {
//    public static Double spoolDiameter = 1.0;
//    static double frictionRPM = 0.0;
//    static PIDParams pidParams = new PIDParams(0.0, 0.0, 0.0, 0.0);
//    static SlideRange slideRange = new SlideRange(0.0, 38.0);
//    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0, frictionRPM);
//    public static SlideMotor slideMotor = new SlideMotor("Slide", DcMotorSimple.Direction.FORWARD,
//            Hardware.YellowJacket.RPM223,
//            spoolDiameter, slideSystemConstants, 1.0, pidParams, slideRange.asArrayList());


    static double frictionRPM = 0.0;
    static double inertia = 0.0;
    static AngleRange angleRange = new AngleRange(0.0, 38.0);
    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(inertia, frictionRPM, new GravityModelConstants(0.0,0.0,0.0));

    public static ArmMotor armMotor = new ArmMotor.Builder("Slide", DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223, pivotSystemConstants, angleRange.asArrayList())
            .pidParams(new PIDParams(0.0, 0.0, 0.0, 0.0))
            .build();






    public static double start = 0.0;
    public static double gravityMotorPower = 0.0;

    static boolean en = false;


    private static OpModeMeta metaForClass(Class<? extends OpMode> cls, String tag) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName() + tag)
                .setGroup("PIDTuners")
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        if (!en) return;
        manager.register(metaForClass(PSODirectionDebugger.class, ""), new PSODirectionDebugger(null, armMotor));
        manager.register(
                metaForClass(FrictionTest.class, "Slide"), new FrictionTest(armMotor)
        );
        manager.register(
                metaForClass(SampleOpMode.class, "Slide"), new SampleOpMode(armMotor)
        );
        manager.register(
                metaForClass(FindPID.class, "Slide"), new FindPID(armMotor)
        );
    }
}


