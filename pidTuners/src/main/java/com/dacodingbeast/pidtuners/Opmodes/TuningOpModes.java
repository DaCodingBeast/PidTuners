//package com.dacodingbeast.pidtuners.Opmodes;
//
//
//import com.dacodingbeast.pidtuners.Constants.GravityModelConstants;
//import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants;
//import com.dacodingbeast.pidtuners.Constants.SlideSystemConstants;
//import com.dacodingbeast.pidtuners.HardwareSetup.ArmMotor;
//import com.dacodingbeast.pidtuners.HardwareSetup.Hardware;
//import com.dacodingbeast.pidtuners.HardwareSetup.SlideMotor;
//import com.dacodingbeast.pidtuners.Simulators.AngleRange;
//import com.dacodingbeast.pidtuners.Simulators.SlideRange;
//import com.dacodingbeast.pidtuners.utilities.DataLogger;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
//import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//
//import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
//
//import CommonUtilities.PIDParams;
//
////todo go through this class and update with TestingSize
//
//public final class TuningOpModes {
//    public static Double spoolDiameter = 1.0;
//    static AngleRange obstacleAngle = AngleRange.Angles.fromDegrees(0, 90);
//    static AngleRange testingAngle = AngleRange.Angles.fromDegrees(90, 180);
//    static double stationaryAngle = Math.toRadians(0.0);
//    static double frictionRPM = 0.0;
//    static PIDParams pidParams = new PIDParams(0.0, 0.0, 0.0, 0.0);
//    static PivotSystemConstants pivotSystemConstants = new PivotSystemConstants(0.0, frictionRPM, new GravityModelConstants(0.0, 0.0, 0.0));
//    public static ArmMotor armMotor = new ArmMotor("Shoulder", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223, pivotSystemConstants,testingAngle.asArrayList(), 1.0, pidParams,  null, obstacleAngle);
//    static SlideRange slideRange = new SlideRange(0.0, 38.0);
//    static SlideRange slideObstacle = null;
//
//    static SlideSystemConstants slideSystemConstants = new SlideSystemConstants(0.0, frictionRPM);
//
//    public static SlideMotor slideMotor = new SlideMotor("Slide", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223, spoolDiameter, slideSystemConstants, 1.0, pidParams, slideRange.asArrayList(), null, slideObstacle);
//    static double gravityMotorPower = 0.5;
//
//    static boolean enableSlides = false;
//    static boolean enableArm = false;
//
//    private TuningOpModes() {
//    }
//
//    private static OpModeMeta metaForClass(Class<? extends OpMode> cls, String tag) {
//        return new OpModeMeta.Builder()
//                .setName(cls.getSimpleName() + tag)
//                .setGroup("PIDTuners")
//                .setFlavor(OpModeMeta.Flavor.TELEOP)
//                .build();
//    }
//
//    @OpModeRegistrar
//    public static void register(OpModeManager manager) {
//        manager.register(metaForClass(PSODirectionDebugger.class, ""), new PSODirectionDebugger(slideMotor, armMotor));
//        if (enableArm) {
//            manager.register(
//                    metaForClass(FrictionTest.class, "Arm"), new FrictionTest(armMotor)
//            );
//            manager.register(
//                    metaForClass(GravityTest.class, "Arm"), new GravityTest(armMotor)
//            );
//            manager.register(
//                    metaForClass(SampleOpMode.class, "Arm"), new SampleOpMode(armMotor)
//            );
//            manager.register(
//                    metaForClass(FindPID.class, "Arm"), new FindPID(armMotor)
//            );
//        }
//        if (enableSlides) {
//            manager.register(
//                    metaForClass(FrictionTest.class, "Slide"), new FrictionTest(slideMotor)
//            );
//            manager.register(
//                    metaForClass(SampleOpMode.class, "Slide"), new SampleOpMode(slideMotor)
//            );
//            manager.register(
//                    metaForClass(FindPID.class, "Slide"), new FindPID(slideMotor)
//            );
//        }
//        DataLogger.create();
//        DataLogger.getInstance().initLogger(enableArm,enableSlides);
//    }
//}
