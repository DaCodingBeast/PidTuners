package org.firstinspires.ftc.teamcode.PSO.Arm

import com.dacodingbeast.pidtuners.Simulators.AngleRange.Angles.fromDegrees
import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.PSO.Arm.Constants.GravityOpMode.gravityConstants
import org.junit.Test
import kotlin.random.Random

class TestingPivotConstants{

    /**
     * Make Sure MOTOR RPMS are reasonable
     * Expected Outcome: Preconditions Triggered to throw exceptions
     */

    @Test
    fun checkIncorrectRPMS(){
        val errorOccured = try{
            PivotSystemConstants(Motor("motor",DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM84, 1.0, null),
                gravityConstants,
                Constants.RotationalInertiaOpmode.Inertia,
                Random.nextDouble(),
            )
            false
        }catch (e: IllegalArgumentException){
            true
        }
        assert(!errorOccured)
    }

}

object Constants {

    object hardware {
        //TODO: Change RPM, Direction, and Name
        val motor = Motor("motor",DcMotorSimple.Direction.FORWARD, Hardware.YellowJacket.RPM84, 1.0, null)
    }

    //todo List Angles Your arm cannot reach due to physical barriers - (IN RADIANS)
    object Obstacles{
        val obstacles = fromDegrees(0.0, 90.0)
    }

    //todo change after running Friction OpMode
    object FrictionOpMode {
        var RPM = 74.9
    }

    //todo change after running Gravity OpMode
    object GravityOpMode {
        var a = -4.5333
        var b = 1.56966
        var k = 11.1867
        val gravityConstants = GravityModelConstants(a,b,k)
    }

    //todo change after running RotationalInertia OpMode
    object RotationalInertiaOpmode {
        @JvmField
        var Inertia = 1.170751047881278
    }

    val constant = PivotSystemConstants(
        hardware.motor,
        gravityConstants,
        FrictionOpMode.RPM,
        RotationalInertiaOpmode.Inertia
    )

}




