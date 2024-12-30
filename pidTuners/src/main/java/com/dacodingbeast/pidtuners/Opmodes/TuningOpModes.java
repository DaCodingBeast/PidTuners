package com.dacodingbeast.pidtuners.Opmodes;

import com.dacodingbeast.pidtuners.CommonUtilities.PivotConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange;
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.GravityModelConstants;
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.PivotSystemConstants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import ArmSpecific.pso4Arms;
import CommonUtilities.PIDFcontroller;
import CommonUtilities.PIDParams;

public final class TuningOpModes {
    private static Motor motor = new Motor("Shoulder", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,1.0,null);

    static AngleRange testingAngle = AngleRange.Angles.fromDegrees(90, 180);

    static AngleRange obstacleAngle = AngleRange.Angles.fromDegrees(0, 90);

    static double stationaryAngle = Math.toRadians(0.0);

    static double frictionRPM = 0.0;

    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(motor, new  GravityModelConstants(0.0,0.0,0.0),0.0,frictionRPM);

    static pso4Arms pso4Arms = new pso4Arms(pivotSystemConstants,testingAngle.asArrayList(),30.0,obstacleAngle,3.5);

    static Boolean gravityRecord = false;

    static Boolean gravityDisplayPoints = false;

    static double gravityMotorPower = 0.5;

    static PIDFcontroller pidfController = new PIDFcontroller(new PIDParams(0.0,0.0,0.0,0.0),motor,obstacleAngle,0.0);

    private static boolean pivotDisabled = false;
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
        if (!pivotDisabled) {
            PivotConstants constants = new PivotConstants(motor, testingAngle, obstacleAngle, pivotSystemConstants, pso4Arms, gravityRecord, gravityDisplayPoints, gravityMotorPower, pidfController);
            manager.register(
                    metaForClass(FrictionTest.class), new FrictionTest(constants)
            );
            manager.register(
                    metaForClass(SampleOpMode.class), new SampleOpMode(constants)
            );
        }
    }

}
