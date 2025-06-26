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

    private val swarmSize = 5000
    val particles = Array(swarmSize) {
        Particle(
            parameterRanges,
            FitnessFunction(time, motor, targetIndex)
        )
    }

    //initialize
    private var gBestParticle = particles[0]

    var lastTimeS = System.currentTimeMillis()/1000  // to seconds
    var thisElapsed: Long = 0


    fun update(times: Int) {
        DataLogger.instance.logDebug("starting update function ")
        for (b in 0 until times) {
            DataLogger.instance.logDebug("starting iteration $b")
            lastTimeS = System.currentTimeMillis()/1000
            for (particle in particles) {
//                DataLogger.instance.logDebug("particle $b")

                //choosing only a few particles to examine
//                val holdData = particles.indexOf(particle) % (50000 / 1) == 0
//                if(holdData) particle.printArmSimStory(b)

                particle.updateVelocity(gBestParticle)

                particle.updateFitness()

                if (particle.bestResult < gBestParticle.bestResult) {
                    gBestParticle = particle
                }
            }
            thisElapsed = (System.currentTimeMillis()/1000 - lastTimeS)
            DataLogger.instance.logData("estimated time remaining (s): ${thisElapsed * (times - b-1)}")
        }
    }

    fun getBest(): Particle {
        return gBestParticle
    }
}