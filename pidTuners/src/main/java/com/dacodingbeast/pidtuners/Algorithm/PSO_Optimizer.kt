package com.dacodingbeast.pidtuners.Algorithm

import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.dacodingbeast.pidtuners.Simulators.Target


/**
 * PSO Simulator, that updates the swarms best position, and provides it to the particles.
 * This effectively moves the swarm towards the better performing PIDF Coefficients
 * @param swarmSize The number of Particles
 * @param randomInRanges The Ranges of the PIDF Coefficients
 * @param angleRange Start and Target Angles
 * @param obstacleAngleRange Obstacle
 * @param multiplePidfs Allows the use of Multiple PIDF Controllers at once
 * @param pBestWeight The Impact of the Particles prev position on particle's new velocity
 * @param prevVeloWeight The Impact of the Particles previous velocity on particle's new velocity
 * @param gBestWeight The Impact of the Swarms best position on the particle's new velocity
 */
//decrease previous when running a ton of swarm


class PSO_Optimizer(
    private val parameterRanges: ArrayList<Ranges>,
    private val simulationType: SimulatorType,
    time: Double,
    targets: Target,
    obstacle: Target
) {
    private val swarmSize = 1000000
    private val particles = Array(swarmSize) { Particle(parameterRanges, FitnessFunction(time,targets,obstacle,simulationType))}

    //initialize
    private var gBestParticle = particles[0]

    private var lastPower = 0.0

    fun update(times: Int): Double {
        (0 until times).forEach { b ->

            for (particle in particles) {

                //choosing only a few particles to examine
                val holdData = particles.indexOf(particle) % (50000/1) ==0
//                if(holdData) particle.printArmSimStory(b)

                if (particle.bestResult < gBestParticle.bestResult) {
                    gBestParticle = particle
                }

                particle.updateVelocity(gBestParticle)

            }


        }
        return lastPower
    }

    fun getBest(): Particle {
        return gBestParticle
    }
}


//todo only have prints be the global best at the end