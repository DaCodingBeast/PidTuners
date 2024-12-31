package com.dacodingbeast.pidtuners.Opmodes;


import com.dacodingbeast.pidtuners.Constants.Constants;
import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.dacodingbeast.pidtuners.Simulators.SimulatorType;
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange;
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants;
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import CommonUtilities.PIDFcontroller;
import CommonUtilities.PIDParams;

public final class TuningOpModes {
    private static Motor motor = new Motor("Shoulder", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,1.0,null);

    static AngleRange testingAngle = AngleRange.Angles.fromDegrees(90, 180);

    static AngleRange obstacleAngle = AngleRange.Angles.fromDegrees(0, 90);

    static double stationaryAngle = Math.toRadians(0.0);

    static double frictionRPM = 0.0;

    static double accuracy = 3.5;

    static double time = 30.0;

    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(0.0,frictionRPM, new  GravityModelConstants(0.0,0.0,0.0));
    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0,frictionRPM);
    static Boolean gravityRecord = false;

    static Boolean gravityDisplayPoints = false;

    static double gravityMotorPower = 0.5;

    static PIDFcontroller pidfController = new PIDFcontroller(new PIDParams(0.0,0.0,0.0,0.0),motor,obstacleAngle,0.0);

    private static SimulatorType simulatorType = SimulatorType.ArmSimulator;
    private TuningOpModes() {
    }

    private static OpModeMeta metaForClass(Class<? extends OpMode> cls) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName())
                .setGroup("PIDTuners")
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
            Constants constants = new Constants(motor, testingAngle.asArrayList(), obstacleAngle.asArrayList(),(simulatorType == SimulatorType.ArmSimulator)  ?pivotSystemConstants : slideSystemConstants);
            manager.register(
                    metaForClass(FrictionTest.class), new FrictionTest(constants)
            );
            if (simulatorType == SimulatorType.ArmSimulator) {
                manager.register(
                        metaForClass(GravityTest.class), new GravityTest(constants)
                );
            }
            manager.register(
                    metaForClass(SampleOpMode.class), new SampleOpMode(constants, pidfController)
            );
            manager.register(
                    metaForClass(FindPID.class), new FindPID(constants,accuracy,simulatorType,time)
            );
    }

}
