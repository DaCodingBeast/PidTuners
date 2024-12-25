package org.firstinspires.ftc.teamcode.PSO.Arm

import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import ArmSpecific.GravityModelConstants
import ArmSpecific.SystemConstants
import org.firstinspires.ftc.teamcode.PSO.Arm.Constants.GravityOpMode.gravityConstants
import org.firstinspires.ftc.teamcode.customHardware.pso.Hardware
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
            SystemConstants(
                Random.nextDouble(),
                Hardware.YellowJacket.RPM223,
                gravityConstants,
                Constants.RotationalInertiaOpmode.Inertia
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
        val motor = Hardware.YellowJacket.RPM84
    }

    //todo List Angles Your arm cannot reach due to physical barriers - (IN RADIANS)
    object Obstacles{
        val obstacles = arrayListOf(AngleRange(0.0,1.0))
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

    val constant = SystemConstants(
        FrictionOpMode.RPM,
        hardware.motor,gravityConstants,
        RotationalInertiaOpmode.Inertia
    )

}




