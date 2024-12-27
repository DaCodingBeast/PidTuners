package com.dacodingbeast.pidtuners.Algorithm

import com.dacodingbeast.pidtuners.Ranges
import kotlin.random.Random


/**
 * Particles are the objects that hold the PID Coefficients
 * @param ranges The ranges that limit the PIDF Coefficients
 * @param multiplePids Boolean that creates 12 PIDF Coefficients for 3 Controllers vs 4 for 1
 */
class Particle(private val ranges: ArrayList<Ranges>, private val multiplePids: Boolean) {
    /**
     * The initialized random position of the Particle.
     * Initialized values are in between [ranges]
     * @see Vector
     */
    var position = Vector(ranges.map{ Random.nextDouble(it.start, it.stop) }.toDoubleArray())
    var velocity: Vector = if(multiplePids) Vector(DoubleArray(12))
    else Vector(DoubleArray(4))
    //initialize at start
    var pBestParam = position
    /**
     * The Best Fitness Value.
     * It is the highest number possible, because the function is minimizing ITAE
     */
    //We are using a minimizing fitness function
    var bestResult = Double.MAX_VALUE

}
