package com.dacodingbeast.pidtuners.Algorithm

import kotlin.math.pow

class QuadraticRegression {
    companion object {
        @JvmStatic
        fun quadraticRegressionManual(x: DoubleArray, y: DoubleArray): DoubleArray {
            val n = x.size

            // Calculate the sums needed for the normal equations
            val sumX = x.sum()
            val sumX2 = x.sumOf { it * it }
            val sumX3 = x.sumOf { it.pow(3) }
            val sumX4 = x.sumOf { it.pow(4) }
            val sumY = y.sum()
            val sumXY = x.zip(y).sumOf { it.first * it.second }
            val sumX2Y = x.zip(y).sumOf { it.first * it.first * it.second }

            // Solve the normal equations
            val matrix = arrayOf(
                doubleArrayOf(n.toDouble(), sumX, sumX2),
                doubleArrayOf(sumX, sumX2, sumX3),
                doubleArrayOf(sumX2, sumX3, sumX4)
            )
            val rhs = doubleArrayOf(sumY, sumXY, sumX2Y)

            // Use Gaussian elimination or any linear solver to solve the system
            val coefficients = solveLinearSystem(matrix, rhs)
            return coefficients
        }

        fun solveLinearSystem(matrix: Array<DoubleArray>, rhs: DoubleArray): DoubleArray {
            val n = matrix.size
            val augmentedMatrix = Array(n) { i -> matrix[i] + doubleArrayOf(rhs[i]) }

            // Forward elimination
            for (i in 0 until n) {
                // Find the pivot row and swap
                var maxRow = i
                for (k in i + 1 until n) {
                    if (kotlin.math.abs(augmentedMatrix[k][i]) > kotlin.math.abs(augmentedMatrix[maxRow][i])) {
                        maxRow = k
                    }
                }
                val temp = augmentedMatrix[i]
                augmentedMatrix[i] = augmentedMatrix[maxRow]
                augmentedMatrix[maxRow] = temp

                // Make the pivot non-zero
                if (kotlin.math.abs(augmentedMatrix[i][i]) < 1e-9) {
                    throw IllegalArgumentException("Matrix is singular or nearly singular.")
                }

                // Eliminate the column below the pivot
                for (k in i + 1 until n) {
                    val factor = augmentedMatrix[k][i] / augmentedMatrix[i][i]
                    for (j in i until n + 1) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j]
                    }
                }
            }

            // Back substitution
            val solution = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                var sum = 0.0
                for (j in i + 1 until n) {
                    sum += augmentedMatrix[i][j] * solution[j]
                }
                solution[i] = (augmentedMatrix[i][n] - sum) / augmentedMatrix[i][i]
            }

            return solution
        }
    }
}