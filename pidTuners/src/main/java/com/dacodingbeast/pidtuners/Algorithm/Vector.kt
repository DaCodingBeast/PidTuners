package com.dacodingbeast.pidtuners.Algorithm

import kotlin.math.abs

/**
 * Changing the PIDF Coefficients [particleParams] through vector translations
 */
class Vector(var particleParams: DoubleArray) {
    private var numOfVelos = particleParams.size

    fun ensureNonNegativePosition(swarmBestPosition: Vector, particlePosition: Vector) {
        for (i in particleParams.indices) {
            if (particleParams[i] < 0) {
                particleParams[i] = if (swarmBestPosition.particleParams[i] < 0) {
                    abs(particlePosition.particleParams[i])
                } else {
                    swarmBestPosition.particleParams[i]
                }
            }
        }
    }

    /**
     * Adding by a vector
     */
    operator fun plus(velo: Vector): Vector {
        val final = DoubleArray(numOfVelos)
        for (i in 0 until numOfVelos) {
            final[i] = particleParams[i] + velo.particleParams[i]
        }
        return Vector(final)
    }

    /**
     * Subtracting by a vector
     */
    operator fun minus(v: Vector): Vector {
        val final = DoubleArray(numOfVelos)
        for (i in 0 until numOfVelos) {
            final[i] = particleParams[i] - v.particleParams[i]
        }
        return Vector(final)
    }

    /**
     * Multiplying by a vector
     */
    operator fun times(v: Double): Vector {
        val final = DoubleArray(numOfVelos)
        for (i in 0 until numOfVelos) {
            final[i] = particleParams[i] * v
        }
        return Vector(final)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in particleParams) {
            sb.append("$i, ")
        }
        if (particleParams.size % 2 == 0) {
            sb.append("0.0")
        }
        return sb.toString()
    }


}