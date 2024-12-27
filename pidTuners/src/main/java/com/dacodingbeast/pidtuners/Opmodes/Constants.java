package com.dacodingbeast.pidtuners.Opmodes;

import static org.firstinspires.ftc.teamcode.App.Constant.*;
import static java.lang.Math.PI;

import com.acmerobotics.dashboard.config.Config;
import com.dacodingbeast.pidtuners.CommonUtilities.Hardware;
import com.dacodingbeast.pidtuners.Arm.AngleRange;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import ArmSpecific.ArmAngle;
import com.dacodingbeast.pidtuners.Arm.GravityModelConstants;
import com.dacodingbeast.pidtuners.Arm.SystemConstants;
import ArmSpecific.pso4Arms;
import CommonUtilities.PIDFParams;
import CommonUtilities.PIDFcontroller;

@Config
public class Constants {

    //CONFIGURATIONS - do before running anything

    //todo Assign Your Motor Specs, direction, and CONFIG Name
    public final Hardware.Motor motor = new Hardware.Motor(117,1425.05923061,68.4,2.0);
    public final DcMotorSimple.Direction motorDirection = DcMotorSimple.Direction.FORWARD;
    public final String motorName = "shoulder";
    public final AngleRange testingAngle = new AngleRange(PI/2, PI/2 +.6);
    //todo provide angles (in radians) that present as obstacles to the system. If none set to null
    public final AngleRange obstacle = new AngleRange(-1.0,PI/2-.5 ); // = null;

    SystemConstants constant = new SystemConstants(
            frictionRPM,
            motor,
            new GravityModelConstants(gravityA, gravityB, gravityK),
            inertiaValue
    );
    public pso4Arms sim = new pso4Arms(constant, angleRanges, 1.2, obstacle, 2.0);
    public static boolean gravityRecord = false;
    public static boolean gravityDisplayDataPoints = false;
    public static double gravityMotorPower = 0.0;
    public PIDFcontroller pidfController = new PIDFcontroller(
            new PIDFParams(0.0, 0.0, 0.0, 0.0),
            motor,
            obstacle,
            PI/2
    );

    //for custom usage and finding of arm angle ->
    public ArmAngle armAngle = new ArmAngle(motor,PI/2);

}

