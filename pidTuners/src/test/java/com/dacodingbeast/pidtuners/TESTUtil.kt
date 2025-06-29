package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.utilities.MathFunctions.removeOutliers

fun MutableList<Long>.verifyData():MutableList<Long> {
    this.removeAt(0)
    val arrayList = ArrayList(this.map { it.toDouble() })
    val newArrayList = removeOutliers(arrayList)
    return newArrayList.map { it.toLong() }.toMutableList()
}