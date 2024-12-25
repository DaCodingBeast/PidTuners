package com.example.pso4pid

import com.dacodingbeast.pidtuners.ArmSpecific.GravityModelConstants
import ArmSpecific.Hardware.Motor
import com.dacodingbeast.pidtuners.ArmSpecific.SystemConstants
import ArmSpecific.pso4Arms
import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import com.dacodingbeast.pidtuners.Algorithm.Particle
import PSO_Algorithm.Ranges
import android.util.Log
import org.junit.Test
import kotlin.random.Random

class PSOtests {


    /**
     * Make sure that particles do not have negative pidf paramters, and that they always have velocities
     */
    @Test
    fun particleParamRanges() {
        for (i in 0..100) {
            val ranges = arrayListOf(Ranges(0.0, Random.nextDouble(0.0, Double.MAX_VALUE)))
            val particle = Particle(ranges, false)
            particle.velocity.particleParams.forEach {
                require(it == 0.0) {
                    Log.d(
                        ArmSpecific.error,
                        "Dm Creator that this error occurred"
                    )
                }
            }
            particle.position.particleParams.forEach {
                require(it > 0.0) {
                    Log.d(
                        ArmSpecific.error,
                        "Dm Arya that this error occurred"
                    )
                }
            }
        }
    }

    @Test
    fun testPSOSIM() {
        //CONFIGURATIONS - do before running anything
        val frictionRPM = 234.99290972461
        val inertiaValue = 0.15895889337822836

        val gravityA = -2.80269
        val gravityB = 1.60163
        val gravityK = 7.13843

        val angleRanges: ArrayList<AngleRange> = arrayListOf(
            AngleRange(Math.PI / 2, Math.PI), AngleRange(Math.PI - .1, Math.PI / 2)
        )

        val motor = Motor(117.0, 1425.05923061, 68.4, 2.0)
        val obstacle = AngleRange(-.5, Math.PI / 2 - .2) // = null;

        val constant = SystemConstants(
            frictionRPM,
            motor,
            GravityModelConstants(gravityA, gravityB, gravityK),
            inertiaValue
        )

        val sim = pso4Arms(constant, angleRanges, 1.0, obstacle, 1.7)
        sim.getPIDFConstants()
    }


}