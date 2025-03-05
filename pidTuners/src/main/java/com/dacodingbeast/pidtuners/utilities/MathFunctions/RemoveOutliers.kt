package com.dacodingbeast.pidtuners.utilities.MathFunctions

import com.dacodingbeast.pidtuners.utilities.DataLogger

fun removeOutliers(data: ArrayList<Double>): ArrayList<Double> {

    if (data.isEmpty()) {
        DataLogger.instance.logError("Remove outliers: Data is empty")
        return ArrayList()
    }

    // Sort the data
    val sortedData = data.sorted()

    // Calculate Q1 and Q3
    val q1 = sortedData[(sortedData.size * 0.25).toInt()]
    val q3 = sortedData[(sortedData.size * 0.75).toInt()]
    val iqr = q3 - q1

    // Calculate the lower and upper bounds
    val lowerBound = q1 - 1.5 * iqr
    val upperBound = q3 + 1.5 * iqr

    // Filter data within the bounds
    return ArrayList(sortedData.filter { it in lowerBound..upperBound })
}