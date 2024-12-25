package com.dacodingbeast.pidtuners.Opmodes;

import com.dacodingbeast.pidtuners.CommonUtilities.Constants;
import com.dacodingbeast.pidtuners.CommonUtilities.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

public final class TuningOpModes {
    private static Hardware.Motor motor = new Hardware.Motor("Shoulder", DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM223,null,null,null);


    private boolean pivotDisabled = false;
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
        Constants constants = new Constants(motor,);
                manager.register(
                        metaForClass(FrictionTest.class),new FrictionTest()
                );
    }

}
