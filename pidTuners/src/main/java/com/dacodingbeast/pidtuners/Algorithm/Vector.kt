package com.dacodingbeast.pidtuners.Algorithm

import kotlin.math.abs

/**
 * Changing the PIDF Coefficients [particleParams] through vector translations
 */
class Vector(var particleParams: DoubleArray) {
    private var numOfVelos = particleParams.size

    fun ensureNonNegativePosition(swarmBestPosition: Vector, particlePosition: Vector) {
        particleParams = particleParams.mapIndexed { index, value ->
            if (value < 0) {
                if (swarmBestPosition.particleParams[index] < 0) abs(particlePosition.particleParams[index])
                else swarmBestPosition.particleParams[index]
            } else value
        }.toDoubleArray()
    }

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
        var returnable = ""
        for (i in particleParams) {
            returnable += "$i, "
        }
        if (returnable.split(",").size % 2 == 0) {
            returnable += "0.0"
        }
        return returnable
    }


}