package com.dacodingbeast.pidtuners.Opmodes;


import com.dacodingbeast.pidtuners.Constants.GravityModelConstants;
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants;
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor;
import com.dacodingbeast.pidtuners.Simulators.AngleRange;
import com.dacodingbeast.pidtuners.Simulators.SlideRange;
import com.dacodingbeast.pidtuners.utilities.DataLogger;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import CommonUtilities.PIDParams;

class PIDTuningOpModes {
    public static Double spoolDiameter = 1.0;
    static double frictionRPMSlide = 0.0;
    static SlideRange slideRange = SlideRange.fromTicks(0.0,700.0);
    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0, frictionRPMSlide);
    public static SlideMotor slideMotor = new SlideMotor.Builder("Slide", DcMotorSimple.Direction.FORWARD,
            new Hardware.HDHex(//TODO
Hardware.HDHexGearRatios.GR5_1
                    ).getMotorSpecs(),
             slideSystemConstants, spoolDiameter,slideRange.asArrayList()).pidParams(0.0,0.0,0.0,0.0).build();


    static double frictionRPM = 0.0;
    static double inertia = 0.0;
    static AngleRange angleRange = AngleRange.fromDegrees(0.0, 45.0);
    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(inertia, frictionRPM, new GravityModelConstants(0.0,0.0,0.0));

    public static ArmMotor armMotor = new ArmMotor.Builder("Arm", DcMotorSimple.Direction.FORWARD,
            Hardware.YellowJacket.RPM223, pivotSystemConstants, angleRange.asArrayList())
            .pidParams(new PIDParams(0.0, 0.0, 0.0, 0.0))
            .build();

    private static OpModeMeta metaForClass(Class<? extends OpMode> cls, String tag) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName() + tag)
                .setGroup("PIDTuners")
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        boolean armEN = false;
        boolean slidesEN = false;
        if (armEN||slidesEN) {
            manager.register(metaForClass(PSODirectionDebugger.class, ""), new PSODirectionDebugger(slideMotor, armMotor));
            DataLogger.getInstance().initLogger(armEN,slidesEN);
        }
        if (armEN) {
            manager.register(
                    metaForClass(FrictionTest.class, "Arm"), new FrictionTest(armMotor,angleRange,slideRange)
            );
            manager.register(
                    metaForClass(SampleOpMode.class, "Arm"), new SampleOpMode(armMotor)
            );
            manager.register(
                    metaForClass(FindPID.class, "Arm"), new FindPID(armMotor)
            );
            manager.register(
                    metaForClass(GravityTest.class, "Arm"), new GravityTest(armMotor,angleRange)
            );
        }
        if (slidesEN){
            manager.register(
                    metaForClass(FrictionTest.class,"Slide"), new SlidesTest(slideMotor)
            );
            manager.register(
                    metaForClass(SampleOpMode.class,"Slide"), new SampleOpMode(slideMotor)
            );
            manager.register(
                    metaForClass(FindPID.class,"Slide"),new FindPID(slideMotor)
            );
        }
    }
}


