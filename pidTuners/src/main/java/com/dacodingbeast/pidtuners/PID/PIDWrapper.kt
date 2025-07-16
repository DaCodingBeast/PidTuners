package com.dacodingbeast.pidtuners.PID

import CommonUtilities.PIDParams
import CommonUtilities.Result
import com.dacodingbeast.pidtuners.Algorithm.Dt
import kotlin.math.abs

interface PIDWrapper {
    var pidParams: PIDParams
    fun kP(): Double = pidParams.kp
    fun kI(): Double = pidParams.ki
    fun kD(): Double = pidParams.kd
    fun kF(): Double = pidParams.kf
    val isSimulator: Boolean
        get() = false

    fun calculate(error: Double, ff: Double): Result
    fun reset()
    class Builder(var pidParams: PIDParams) {
        private var resetFun: (() -> Unit)? = null
        private var calcFun: ((Double, Double) -> Result)? = null
        fun calculate(calcFunV: (Double, Double) -> Result): Builder {
            this.calcFun = calcFunV
            return this
        }

        fun reset(resetFun: () -> Unit): Builder {
            this.resetFun = resetFun
            return this
        }

        fun build(): PIDWrapper {
            require(calcFun != null && resetFun != null) { "Both calculate or reset should be provided" }
            return PIDWrapperImpl(pidParams, calcFun!!, resetFun!!)
        }

    }
}

class PIDWrapperImpl(
    override var pidParams: PIDParams,
    val calcFun: (Double, Double) -> Result,
    val resetFun: () -> Unit
) : PIDWrapper {
    override fun calculate(error: Double, ff: Double): Result {
        return calcFun(error, ff)
    }

    override fun reset() {
        resetFun()
    }
}


class THISPIDWrapperImpl(override var pidParams: PIDParams) : PIDWrapper {
    private var prevError = 0.0
    private var integral = 0.0

    private val dtInverse = 1.0 / Dt
    private val errorNormalizationFactor = 1.0 / 10.0
    override fun calculate(error: Double, ff: Double): Result {
        integral += error * Dt
        val derivative = (error - prevError) * errorNormalizationFactor * dtInverse
        prevError = error

        val controlEffort =
            (error * pidParams.kp + integral * pidParams.ki + derivative * pidParams.kd + ff * pidParams.kf)
                .coerceIn(-1.0, 1.0)

        return Result(controlEffort, error, this.javaClass)
    }

    override fun reset() {
        prevError = 0.0
        integral = 0.0
    }

}


class PIDAcmeTest() {
    private var setPoint: Double = 0.0
    private var measuredValue: Double = 0.0
    private var minIntegral: Double = -1.0
    private var maxIntegral: Double = 1.0

    private var errorValP: Double = 0.0
    private var errorValV: Double = 0.0
    private var totalError: Double = 0.0
    private var prevErrorVal: Double = 0.0

    private var lastTimeStamp: Double = 0.0
    private var period: Double = 0.0
    private var pidParams: PIDParams = PIDParams(1.0, 0.0, 0.0, 0.0)


    val pidController = PIDWrapper.Builder(
        pidParams
    )
        .calculate { current, target ->
            prevErrorVal = errorValP

            val currentTimeStamp = System.nanoTime() / 1E9
            if (lastTimeStamp == 0.0) lastTimeStamp = currentTimeStamp
            period = currentTimeStamp - lastTimeStamp
            lastTimeStamp = currentTimeStamp

            errorValP = target - current
            measuredValue = current

            errorValV = if (abs(period) > 1E-6) {
                (errorValP - prevErrorVal) / period
            } else {
                0.0
            }

            totalError += period * (setPoint - measuredValue)
            totalError = totalError.coerceIn(minIntegral, maxIntegral)
            val power =
                pidParams.kp * errorValP + pidParams.ki * totalError + pidParams.ki * errorValV + pidParams.kf * setPoint
            Result.of(power, errorValV, this.javaClass)
        }
        .reset {
            totalError = 0.0
            prevErrorVal = 0.0
            lastTimeStamp = 0.0
        }
        .build()
}

class PIDAcmeImpl(override var pidParams: PIDParams) : PIDWrapper {
    private var setPoint: Double = 0.0
    private var measuredValue: Double = 0.0
    private var minIntegral: Double = -1.0
    private var maxIntegral: Double = 1.0

    private var errorValP: Double = 0.0
    private var errorValV: Double = 0.0
    private var totalError: Double = 0.0
    private var prevErrorVal: Double = 0.0

    private var lastTimeStamp: Double = 0.0
    private var period: Double = 0.0
    override fun calculate(target: Double, current: Double): Result {
        prevErrorVal = errorValP

        val currentTimeStamp = System.nanoTime() / 1E9
        if (lastTimeStamp == 0.0) lastTimeStamp = currentTimeStamp
        period = currentTimeStamp - lastTimeStamp
        lastTimeStamp = currentTimeStamp

        errorValP = target - current
        measuredValue = current

        errorValV = if (abs(period) > 1E-6) {
            (errorValP - prevErrorVal) / period
        } else {
            0.0
        }

        totalError += period * (setPoint - measuredValue)
        totalError = totalError.coerceIn(minIntegral, maxIntegral)
        val power = kP() * errorValP + kI() * totalError + kD() * errorValV + kF() * setPoint
        return Result.of(power, errorValV, this.javaClass)
    }

    override fun reset() {
        totalError = 0.0
        prevErrorVal = 0.0
        lastTimeStamp = 0.0
    }
}
