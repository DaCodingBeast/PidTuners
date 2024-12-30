package com.dacodingbeast.pidtuners.Opmodes;

import com.dacodingbeast.pidtuners.Constants.SlideConstants;
import com.dacodingbeast.pidtuners.HardwareSetup.Motor;
import com.dacodingbeast.pidtuners.TypeSpecific.Slides.SlideRange;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import ArmSpecific.pso4Slides;

public class SlideTestingOpMode extends LinearOpMode {
    SlideConstants constants;
    pso4Slides pso4Slides;
    public SlideTestingOpMode(SlideConstants constants) {
        this.constants = constants;
        pso4Slides = new pso4Slides(constants.getSlideSystemConstants(),constants.getTarget().asArrayList(),30.0,constants.getObstacle(),3.5 );
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
