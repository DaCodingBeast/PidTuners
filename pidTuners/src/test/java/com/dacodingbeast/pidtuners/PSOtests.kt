package com.example.pso4pid

import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
import ArmSpecific.pso4Arms
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Algorithm.Particle
import android.util.Log
import com.dacodingbeast.pidtuners.Algorithm.FitnessFunction
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import com.dacodingbeast.pidtuners.HardwareSetup.Motor
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.HardwareSetup.MotorSpecs
import com.dacodingbeast.pidtuners.HardwareSetup.StallTorque
import com.dacodingbeast.pidtuners.HardwareSetup.TorqueUnit
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.random.Random

class PSOtests {


    /**
     * Make sure that particles do not have negative pidf paramters, and that they always have velocities
     */
    @Test
    fun particleParamRanges() {
        for (i in 0..100) {
            val ranges = arrayListOf(Ranges(0.0, Random.nextDouble(0.0, Double.MAX_VALUE)))
            val particle = Particle(ranges, FitnessFunction(2.0, AngleRange.fromRadians(0.0, PI /2), AngleRange.fromRadians(
                PI /6,
                PI /4),
                SimulatorType.ArmSimulator)
            )
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
            AngleRange.fromRadians(Math.PI / 2, Math.PI), AngleRange.fromRadians(Math.PI - .1, Math.PI / 2)
        )

        val motor = Motor(
            "motor", DcMotorSimple.Direction.FORWARD, MotorSpecs(300.0,
                StallTorque(0.1, TorqueUnit.KILOGRAM_CENTIMETER), 1.0, 28.0
            ), 2.0, null
        )
        val obstacle = AngleRange.fromRadians(-.5, Math.PI / 2 - .2) // = null;

        val constant = PivotSystemConstants(
            motor,
            GravityModelConstants(gravityA, gravityB, gravityK),
            inertiaValue,
            frictionRPM,
        )

        val sim = pso4Arms(constant, angleRanges, 1.0, obstacle, 1.7)
        sim.getPIDFConstants()
    }

        @Test
        fun testUpdate() {
            val parameterRanges = arrayListOf(Ranges(0.0, 1.0))
            val simulationType = SimulatorType.ArmSimulator
            val time = 1.0
            val targets = AngleRange.fromRadians(0.0, 1.0)
            val obstacle = AngleRange.fromRadians(0.5, 0.75)

            val optimizer = PSO_Optimizer(parameterRanges, simulationType, time, targets, obstacle)
            val result = optimizer.update(10)

            assertNotNull(optimizer.getBest())
        }

        @Test
        fun testGetBest() {
            val parameterRanges = arrayListOf(Ranges(0.0, 1.0))
            val simulationType = SimulatorType.ArmSimulator
            val time = 1.0
            val targets = AngleRange.fromRadians(0.0, 1.0)
            val obstacle = AngleRange.fromRadians(0.5, 0.75)

            val optimizer = PSO_Optimizer(parameterRanges, simulationType, time, targets, obstacle)
            optimizer.update(10)
            val bestParticle = optimizer.getBest()

            assertNotNull(bestParticle)
            assertTrue(bestParticle.bestResult >= 0.0)
        }



}