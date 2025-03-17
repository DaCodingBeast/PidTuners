package com.dacodingbeast.pidtuners.Opmodes;


import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor;
import com.dacodingbeast.pidtuners.Simulators.SlideRange;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import CommonUtilities.PIDParams;

public final class TestingSize {
    public static Double spoolDiameter = 1.0;
    static double frictionRPM = 0.0;
    static PIDParams pidParams = new PIDParams(0.0, 0.0, 0.0, 0.0);
    static SlideRange slideRange = new SlideRange(0.0, 38.0);
    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0, frictionRPM);
    public static SlideMotor slideMotor = new SlideMotor("Slide", DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223,
            spoolDiameter, slideSystemConstants, 1.0, pidParams, slideRange.asArrayList());




    private static OpModeMeta metaForClass(Class<? extends OpMode> cls, String tag) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName() + tag)
                .setGroup("PIDTuners")
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        manager.register(metaForClass(PSODirectionDebugger.class, ""), new PSODirectionDebugger(slideMotor));
        manager.register(
                metaForClass(FrictionTest.class, "Slide"), new FrictionTest(slideMotor)
        );
        manager.register(
                metaForClass(SampleOpMode.class, "Slide"), new SampleOpMode(slideMotor)
        );
        manager.register(
                metaForClass(FindPID.class, "Slide"), new FindPID(slideMotor)
        );
    }
}


