package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.Algorithm.Dt
import com.dacodingbeast.pidtuners.Algorithm.Vector
import com.dacodingbeast.pidtuners.Simulators.AngleRange
import com.dacodingbeast.pidtuners.Simulators.SlideRange
import com.dacodingbeast.pidtuners.Simulators.Target
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * PIDF coefficients
 * @param kp Proportional Term
 * @param ki Integral Term
 * @param kd Derivative Term
 * @param kf FeedForward Term, used for fighting gravity forces based on angle
 */
class PIDParams(val kp: Double, val ki: Double, val kd: Double, val kf: Double = 0.0) {
    constructor(params: Vector) : this(
        params.particleParams[0],
        params.particleParams[1],
        params.particleParams[2],
        params.particleParams.getOrNull(3) ?: 0.0
    )
}


/**
 * PIDF controller
 * @param params PIDF coefficients
 * @see PIDParams
 */
class Result(val motorPower: Double, val error: Double,val sign: Class<*>) {

    companion object {
        @JvmStatic
        fun of(motorPower: Double, error: Double,sign:Class<*>) = Result(motorPower, error,sign)
    }
}

////class PIDFcontroller(var params: PIDParams, val isSimulator: Boolean = false) {
////
////    private var prevError = 0.0
////    private var integral = 0.0
////
////    // Pre-calculated constants
////    private val dtInverse = 1.0 / Dt
////    private val hasFF = params.kf != 0.0
////
////    private var minIntegral: Double = -0.5
////    private var maxIntegral: Double = 0.5
////
////    private lateinit var timer: ElapsedTime
////
////    init {
////        if (!isSimulator) timer = ElapsedTime()
////    }
////
////    private inline fun getLoopTime(): Double {
////        return if (isSimulator) {
////            Dt
////        } else {
////            timer.seconds()
////        }
////    }
////
////    private inline fun getLoopTimeInverse(): Double {
////        return if (isSimulator) {
////            dtInverse
////        } else {
////            1 / (timer.seconds())
////        }
////    }
////
////    fun calculate(position: Target, obstacle: Target?): Result {
////
////        when (position) {
////            is AngleRange -> {
////                val (_, error) = AngleRange.findDirectionAndError(position, obstacle as AngleRange?)
////
////                val ff = if (hasFF) {
////                    val sinVal = sin(position.start)
////                    if (position.start > 0.0) max(0.0, sinVal) * params.kf
////                    else min(0.0, sinVal) * params.kf
////                } else 0.0
////
////                return calculateControl(error, ff)
////            }
////
////            is SlideRange -> {
////                val error = position.stop - position.start
////                return calculateControl(error, 0.0)
////            }
////        }
////    }
////
////    private inline fun calculateControl(error: Double, ff: Double): Result {
////        integral += error * getLoopTime()
////        integral = integral.coerceIn(minIntegral, maxIntegral)
////
////        val derivative = (error - prevError) * getLoopTimeInverse()
////        prevError = error
////
////        val controlEffort = (error * params.kp + integral * params.ki + derivative * params.kd + ff)
////            .coerceIn(-1.0, 1.0)
////
////        // Debug: Print PID calculation details
//////        println("=== PID Calculation ===")
//////        println("Error: $error")
//////        println("Previous Error: $prevError")
//////        println("Integral: $integral")
//////        println("Derivative: $derivative")
//////        println("Feedforward: $ff")
//////        println("PID Parameters - P: ${params.kp}, I: ${params.ki}, D: ${params.kd}, F: ${params.kf}")
//////        println("Raw Control Effort: ${error * params.kp + integral * params.ki + derivative * params.kd + ff}")
//////        println("Clamped Control Effort: $controlEffort")
//////        println("Loop Time: ${getLoopTime()} s")
//////        println("================================")
////
////        if (!isSimulator) timer.reset()
////
////        return Result(controlEffort, error,this.javaClass)
////    }
//
//    fun reset() {
//        prevError = 0.0
//        integral = 0.0
//    }
//}


class PIDFcontroller(var params: PIDParams) {

    private var prevError = 0.0
    private var integral = 0.0

    // Pre-calculated constants
    private val dtInverse = 1.0 / Dt
    private val errorNormalizationFactor = 1.0 / 10.0
    private val hasFF = params.kf != 0.0

    fun calculate(position: Target, obstacle: Target?): Result {

        when (position) {
            is AngleRange -> {
                val (_, error) = AngleRange.findDirectionAndError(position, obstacle as AngleRange?)

                val ff = if (hasFF) {
                    val sinVal = sin(position.start)
                    if (position.start > 0.0) max(0.0, sinVal)
                    else min(0.0, sinVal)
                } else 0.0

                return calculateControl(error, ff)
            }

            is SlideRange -> {
                val error = position.stop - position.start
                return calculateControl(error, 0.0)
            }
        }
    }

    private inline fun calculateControl(error: Double, ff: Double): Result {
        integral += error * Dt
        val derivative = (error - prevError) * errorNormalizationFactor * dtInverse
        prevError = error

        val controlEffort = (error * params.kp + integral * params.ki + derivative * params.kd + ff)
            .coerceIn(-1.0, 1.0)

        return Result(controlEffort, error,this.javaClass)
    }

    fun reset() {
        prevError = 0.0
        integral = 0.0
    }
}


////////!acme
//open class PIDFController(var kP: Double, var kI: Double, var kD: Double, var kF: Double) {
//    private var setPoint: Double = 0.0
//    private var measuredValue: Double = 0.0
//    private var minIntegral: Double = -1.0
//    private var maxIntegral: Double = 1.0
//
//    private var errorValP: Double = 0.0
//    private var errorValV: Double = 0.0
//    private var totalError: Double = 0.0
//    private var prevErrorVal: Double = 0.0
//
//    private var errorToleranceP: Double = 0.05
//    private var errorToleranceV: Double = Double.POSITIVE_INFINITY
//
//    private var lastTimeStamp: Double = 0.0
//    private var period: Double = 0.0
//
//    constructor(kP: Double, kI: Double, kD: Double, kF: Double, sp: Double, pv: Double) : this(
//        kP,
//        kI,
//        kD,
//        kF
//    ) {
//        setPoint = sp
//        measuredValue = pv
//        errorValP = setPoint - measuredValue
//        reset()
//    }
//
//    constructor(pidfCoefficients: PIDFCoefficients) : this(
//        pidfCoefficients.kP,
//        pidfCoefficients.kI,
//        pidfCoefficients.kD,
//        pidfCoefficients.kF
//    ) {
//        reset()
//    }
//
//    constructor() : this(0.0, 0.0, 0.0, 0.0) {
//        reset()
//    }
//
//    fun reset() {
//        totalError = 0.0
//        prevErrorVal = 0.0
//        lastTimeStamp = 0.0
//    }
//
//    fun setTolerance(
//        positionTolerance: Double,
//        velocityTolerance: Double = Double.POSITIVE_INFINITY
//    ) {
//        errorToleranceP = positionTolerance
//        errorToleranceV = velocityTolerance
//    }
//
//    fun getSetPoint(): Double = setPoint
//
//    fun setSetPoint(sp: Double) {
//        setPoint = sp
//        errorValP = setPoint - measuredValue
//        errorValV = (errorValP - prevErrorVal) / period
//    }
//
//    fun atSetPoint(): Boolean {
//        return abs(errorValP) < errorToleranceP && abs(errorValV) < errorToleranceV
//    }
//
//    fun getCoefficients(): DoubleArray = doubleArrayOf(kP, kI, kD, kF)
//
//    fun getPositionError(): Double = errorValP
//
//    fun getTolerance(): DoubleArray = doubleArrayOf(errorToleranceP, errorToleranceV)
//
//    fun getVelocityError(): Double = errorValV
//
//    fun calculate(): Double = calculate(measuredValue)
//
//    fun calculate(pv: Double, sp: Double): Double {
//        setSetPoint(sp)
//        return calculate(pv)
//    }
//
//    fun calculate(pv: Double): Double {
//        prevErrorVal = errorValP
//
//        val currentTimeStamp = System.nanoTime() / 1E9
//        if (lastTimeStamp == 0.0) lastTimeStamp = currentTimeStamp
//        period = currentTimeStamp - lastTimeStamp
//        lastTimeStamp = currentTimeStamp
//
//        errorValP = setPoint - pv
//        measuredValue = pv
//
//        errorValV = if (abs(period) > 1E-6) {
//            (errorValP - prevErrorVal) / period
//        } else {
//            0.0
//        }
//
//        totalError += period * (setPoint - measuredValue)
//        totalError = totalError.coerceIn(minIntegral, maxIntegral)
//
//        return kP * errorValP + kI * totalError + kD * errorValV + kF * setPoint
//    }
//
//    fun setPIDF(kP: Double, kI: Double, kD: Double, kF: Double) {
//        this.kP = kP
//        this.kI = kI
//        this.kD = kD
//        this.kF = kF
//    }
//
//    fun setPIDF(pidfCoefficients: PIDFCoefficients) {
//        this.kP = pidfCoefficients.kP
//        this.kI = pidfCoefficients.kI
//        this.kD = pidfCoefficients.kD
//        this.kF = pidfCoefficients.kF
//    }
//
//    fun setIntegrationBounds(integralMin: Double, integralMax: Double) {
//        minIntegral = integralMin
//        maxIntegral = integralMax
//    }
//
//    fun clearTotalError() {
//        totalError = 0.0
//    }
//}