package com.dacodingbeast.pidtuners.Opmodes;

import com.dacodingbeast.pidtuners.Constants.Constants;
import com.dacodingbeast.pidtuners.Constants.SlideConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideRange;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import ArmSpecific.pso4Slides;

public class SlideTestingOpMode extends LinearOpMode {
    Constants constants;
    public SlideTestingOpMode(Constants constants) {
        this.constants = constants;
    }
    @Override
    public void runOpMode() throws InterruptedException {
        Motor motor = constants.getMotor();

        motor.init(hardwareMap, constants.getTarget().getStart());

        if (constants.getObstacle() != null) {
        // inverse because we are checking if the angle is in the obstacle range
        if (!SlideRange.Slides.inRange(constants.getObstacle(), constants.getTarget())) {
            throw new IllegalArgumentException("Angle " + constants.getTarget() + " is in the obstacle range");
        }

}
        waitForStart();

    }
}
