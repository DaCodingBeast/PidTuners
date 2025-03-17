package com.dacodingbeast.pidtuners.Simulators

class SlideRange (override val start: Double, override val stop: Double) :
    Target(start, stop) {

    fun inRange(goal: SlideRange, obstacle: SlideRange): Boolean {
        return goal.start in obstacle.start..obstacle.stop || goal.stop in obstacle.start..obstacle.stop
    }

    fun asArrayList(): ArrayList<SlideRange> {
        return arrayListOf(this)
    }
}
