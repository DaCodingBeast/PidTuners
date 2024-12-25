package com.dacodingbeast.pidtuners.Algorithm

import kotlin.math.abs

/**
 * Changing the PIDF Coefficients [particleParams] through vector translations
 */
class Vector(val particleParams: DoubleArray) {
    private var numOfVelos = particleParams.size

    /**
     * Adding by a vector
     */
    operator fun plus(velo: Vector): Vector {
        val final = DoubleArray(numOfVelos)
        0.until(numOfVelos).forEach { final[it] = particleParams[it] + velo.particleParams[it] }
        return Vector(final)
    }
    /**
     * Subtracting by a vector
     */
    operator fun minus(v: Vector): Vector {
        val final = DoubleArray(numOfVelos)
        0.until(numOfVelos).forEach { final[it] = particleParams[it] - v.particleParams[it] }
        return Vector(final)
    }
    /**
     * Multiplying by a vector
     */
    operator fun times(v: Double): Vector {
        val final = DoubleArray(numOfVelos)
        0.until(numOfVelos).forEach { final[it] = particleParams[it] * v }
        return Vector(final)
    }

    override fun toString(): String {
        return "${particleParams[0]}, ${particleParams[1]}, ${particleParams[2]}, ${particleParams[3]}"
    }
    fun makePositive(): Vector {
        return Vector(particleParams.map { abs(it) }.toDoubleArray())
    }

    fun isNegative(): Boolean{
        return particleParams.any { it<0 }
    }
}