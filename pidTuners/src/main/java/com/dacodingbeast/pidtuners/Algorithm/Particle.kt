package com.dacodingbeast.pidtuners.Algorithm

import kotlin.random.Random


/**
 * Particles are the objects that hold the PID Coefficients
 * @param ranges The ranges that limit the PIDF Coefficients
 */
class Particle(private val ranges: List<Ranges>, private val fitnessFunction: FitnessFunction) {
    /**
     * The initialized random position of the Particle.
     * Initialized values are in between [ranges]
     * @see Vector
     */
    var position = Vector(DoubleArray(ranges.size).apply {
        for (i in indices) {
            this[i] = Random.nextDouble(ranges[i].start, ranges[i].stop)
        }
    })
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
            bestResult = fitness.itae
        }
    }

    /**
     * Update the velocity and angles for each circle based on PSO rules
     */
    fun updateVelocity(globalBest: Particle) {
        // Improved coefficients for better convergence
        val prevVeloCoeffecient = 0.7  // Increased from 0.05 for better momentum
        val particleBestCoefficient = 1.5  // Increased from 0.1 for better local search
        val swarmBestCoefficient = 1.5  // Increased from 0.2 for better global search

        velocity = ((velocity * prevVeloCoeffecient) +
                ((pBestParam - position) * particleBestCoefficient * Random.nextDouble()) +
                ((globalBest.pBestParam - position) * swarmBestCoefficient * Random.nextDouble()))
        
        position += velocity
        
        // Ensure position stays within bounds
        position.clampToRanges(ranges)
    }

    //to show algorithm in csv style (for a python script)
    fun printStory(timeOfOptimization: Int) {
        for (c in 0 until fitness.history.size) {
            //todo format csv for python visual. It should include all of Simulator Data's components
        }
    }

    override fun toString(): String {
        return position.toString()
    }

}
