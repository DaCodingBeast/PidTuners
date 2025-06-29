package com.dacodingbeast.pidtuners.utilities.MathFunctions

import com.dacodingbeast.pidtuners.utilities.DataLogger

fun removeOutliers(data: ArrayList<Double>): ArrayList<Double> {
    if (data.isEmpty()) {
        DataLogger.instance.logError("Remove outliers: Data is empty")
        return ArrayList()
    }

    // For small datasets, use a simple approach
    if (data.size <= 10) {
        return removeOutliersSimple(data)
    }

    // For larger datasets, use a more efficient approach
    return removeOutliersEfficient(data)
}

private fun removeOutliersSimple(data: ArrayList<Double>): ArrayList<Double> {
    val sortedData = data.sorted()
    val size = sortedData.size
    val q1Index = (size * 0.25).toInt()
    val q3Index = (size * 0.75).toInt()
    
    val q1 = sortedData[q1Index]
    val q3 = sortedData[q3Index]
    val iqr = q3 - q1
    
    val lowerBound = q1 - 1.5 * iqr
    val upperBound = q3 + 1.5 * iqr
    
    val result = ArrayList<Double>(size)
    for (value in data) {
        if (value >= lowerBound && value <= upperBound) {
            result.add(value)
        }
    }
    return result
}

private fun removeOutliersEfficient(data: ArrayList<Double>): ArrayList<Double> {
    val size = data.size
    
    // Use a more efficient percentile calculation
    val q1 = findPercentile(data, 0.25)
    val q3 = findPercentile(data, 0.75)
    val iqr = q3 - q1
    
    val lowerBound = q1 - 1.5 * iqr
    val upperBound = q3 + 1.5 * iqr
    
    // Pre-allocate result array with estimated size
    val result = ArrayList<Double>(size)
    for (value in data) {
        if (value >= lowerBound && value <= upperBound) {
            result.add(value)
        }
    }
    return result
}

private fun findPercentile(data: ArrayList<Double>, percentile: Double): Double {
    val size = data.size
    val index = (size * percentile).toInt()
    
    // Use a more efficient selection algorithm for larger datasets
    if (size > 1000) {
        return quickSelectPercentile(data.toDoubleArray(), index)
    } else {
        // For smaller datasets, sorting is actually faster
        val sorted = data.sorted()
        return sorted[index]
    }
}

private fun quickSelectPercentile(arr: DoubleArray, k: Int): Double {
    var left = 0
    var right = arr.size - 1
    
    while (left < right) {
        val pivotIndex = partition(arr, left, right)
        
        when {
            k == pivotIndex -> return arr[k]
            k < pivotIndex -> right = pivotIndex - 1
            else -> left = pivotIndex + 1
        }
    }
    
    return arr[left]
}

private fun partition(arr: DoubleArray, left: Int, right: Int): Int {
    val pivot = arr[right]
    var i = left - 1
    
    for (j in left until right) {
        if (arr[j] <= pivot) {
            i++
            val temp = arr[i]
            arr[i] = arr[j]
            arr[j] = temp
        }
    }
    
    val temp = arr[i + 1]
    arr[i + 1] = arr[right]
    arr[right] = temp
    
    return i + 1
}