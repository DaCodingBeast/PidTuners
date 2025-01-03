package com.dacodingbeast.pidtuners.Algorithm

import com.dacodingbeast.pidtuners.HardwareSetup.Motors
import com.dacodingbeast.pidtuners.Simulators.SimulatorType

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

    private val swarmSize = 1000000
    val particles = Array(swarmSize) {
        Particle(
            parameterRanges,
            FitnessFunction(time, motor.targets[targetIndex], motor, targetIndex)
        )
    }

    //initialize
    private var gBestParticle = particles[0]

    fun update(times: Int) {
        (0 until times).forEach { b ->

            for (particle in particles) {

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


//todo only have prints be the global best at the end