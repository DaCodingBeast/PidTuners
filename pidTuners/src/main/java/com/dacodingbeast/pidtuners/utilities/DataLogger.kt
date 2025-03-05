package com.dacodingbeast.pidtuners.utilities

import com.qualcomm.robotcore.util.RobotLog

class DataLogger private constructor(val tagger: String){
    fun initLogger(a: Boolean,s: Boolean){
        if (a && s) logData("Initializing DataLogger with Arm and Slide")
        else if (a) logData("Initializing DataLogger with Arm")
        else if (s) logData("Initializing DataLogger with Slide")
        else
        logWarning("Initializing DataLogger with NOTHING")
    }
    fun logData(data: Any){
        RobotLog.ii(tagger,data.toString())
    }
    fun logError(data: Any){
        RobotLog.ee(tagger,data.toString())
    }
    fun logWarning(data: Any){
        RobotLog.ww(tagger,data.toString())
    }
    fun logDebug(data: Any){
        RobotLog.dd(tagger,data.toString())
    }
    fun startLogger(name: String){
        logData("Starting OpMode: $name")
    }
    fun endLogger(name: String){
        logData("Ending OpMode: $name")
    }
    companion object{
        @JvmStatic
        val defaultTagger = "PidTunersDataLogger"
        @JvmStatic
        var instance : DataLogger = DataLogger(defaultTagger)
        @JvmStatic
        fun create(){
            this.instance = DataLogger(defaultTagger)
        }
        @JvmStatic
        fun create(tag: String){
            this.instance = DataLogger(tag)
        }
    }
}
