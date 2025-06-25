package com.dacodingbeast.pidtuners.Algorithm

import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.utilities.DataLogger

class PSO_Optimizer(
    private val parameterRanges: ArrayList<Ranges>,
    time: Double,
    motor: Motors,
    targetIndex: Int
) {
    init {
        Companion.motor = motor
    }

    companion object {
        lateinit var motor: Motors
    }

    private val swarmSize = 100000
    val particles = Array(swarmSize) {
        Particle(
            parameterRanges,
            FitnessFunction(time, motor, targetIndex)
        )
    }

    //initialize
    private var gBestParticle = particles[0]

    fun update(times: Int) {
        DataLogger.instance.logDebug("starting update function ")
        (0 until times).forEach { b ->
            DataLogger.instance.logDebug("starting iteration $b")

            for (particle in particles) {
                DataLogger.instance.logDebug("particle $b")

                //choosing only a few particles to examine
                val holdData = particles.indexOf(particle) % (50000 / 1) == 0
//                if(holdData) particle.printArmSimStory(b)

                if (particle.bestResult < gBestParticle.bestResult) {
                    gBestParticle = particle
                }

                particle.updateVelocity(gBestParticle)

            }


        }
    }

    fun getBest(): Particle {
        return gBestParticle
    }
}