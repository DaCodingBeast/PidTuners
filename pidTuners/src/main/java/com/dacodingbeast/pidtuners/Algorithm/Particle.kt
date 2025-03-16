package com.dacodingbeast.pidtuners.Algorithm

import kotlin.random.Random


/**
 * Particles are the objects that hold the PID Coefficients
 * @param ranges The ranges that limit the PIDF Coefficients
 * @param multiplePids Boolean that creates 12 PIDF Coefficients for 3 Controllers vs 4 for 1
 */
class Particle(private val ranges: List<Ranges>, private val fitnessFunction: FitnessFunction) {
    /**
     * The initialized random position of the Particle.
     * Initialized values are in between [ranges]
     * @see Vector
     */
    var position = Vector(ranges.map { Random.nextDouble(it.start, it.stop) }.toDoubleArray())
    var velocity: Vector = Vector(DoubleArray(ranges.size))

    //initialize at start
    var pBestParam = position

    /**
     * The Best Fitness Value.
     * It is the highest number possible, because the function is minimizing ITAE
     */
    //We are using a minimizing fitness function
    var bestResult = Double.MAX_VALUE


    private lateinit var fitness: FitnessFunctionData

    fun updateFitness() {
        fitness = fitnessFunction.findFitness(this) // ITAE
        if (fitness.itae < bestResult) {
            pBestParam = position
        }
    }

    /**
     * Update the velocity and angles for each circle based on PSO rules
     */
    fun updateVelocity(globalBest: Particle) {
        val prevVeloCoeffecient = 0.05
        val particleBestCoefficient = 0.1
        val swarmBestCoefficient = 0.2

        velocity = ((velocity * prevVeloCoeffecient) +
                ((pBestParam - position) * particleBestCoefficient * Random.nextDouble()) +
                ((globalBest.pBestParam - pBestParam) * swarmBestCoefficient * Random.nextDouble()))

        velocity.ensureNonNegativePosition(globalBest.position, position)
        position += velocity
    }

    //to show algorithm in csv style (for a python script)
    fun printStory(timeOfOptimization: Int) {
        (0 until fitness.history.size).forEach { c ->
            //todo format csv for python visual. It should include all of Simulator Data's components
        }
    }

    override fun toString(): String {
        return position.toString()
    }

}
