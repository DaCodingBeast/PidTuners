package com.dacodingbeast.pidtuners.HardwareSetup

import com.dacodingbeast.pidtuners.utilities.DataLogger
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * Represents an analog encoder with a list of operations to perform on the input voltage.
 *
 * @param name The name of the encoder.
 * @param calculatorOperations The list of operations to perform on the input voltage.
 */
class AnalogEncoder(
    override val name: String,
    private val startPosition: Double,
    private val calculatorOperations: List<Operation>
) : Encoders(name) {
    constructor(name: String, startPosition: Double, vararg calculatorOperations: Operation) : this(
        name,
        startPosition,
        calculatorOperations.toList()
    )

    constructor(name: String, vararg calculatorOperations: Operation) : this(
        name,
        0.0,
        calculatorOperations.toList()
    )

    constructor(name: String, calculatorOperations: List<Operation>) : this(
        name,
        0.0,
        calculatorOperations
    )

    private lateinit var analogEncoder: AnalogInput
    private lateinit var calculator: AnalogEncoderCalculator
    override fun init(ahwMap: HardwareMap) {
        analogEncoder = ahwMap.get(AnalogInput::class.java, name)
        calculator = AnalogEncoderCalculator(calculatorOperations)
    }

    override fun getCurrentPosition(): Int {
        return calculator.runOperations(analogEncoder.voltage).toInt() - startPosition.toInt()
    }
}

/**
 * Represents a calculator that performs a list of operations on an input value.
 *
 * @param operations The list of operations in order to perform.
 */
class AnalogEncoderCalculator(val operations: List<Operation>) {
    init {
        try {
            require(operations.isNotEmpty()) { "Operations cannot be empty" }
        }catch (_: Exception){
            DataLogger.instance.logError("Analog Encoder Calculator: Operations cannot be empty")
        }
    }

    /**
     * Runs the operations on the input value.
     *
     * @param input The input value to perform the operations on.
     * @return The result after performing all the operations.
     */
    fun runOperations(input: Double): Double {
        var returnable = input
        operations.forEach {
            returnable = it.runOperation(returnable)
        }
        return returnable
    }
}

class Operation(val operation: Operand, val value: Double) {
    /**
     * Runs the operation on the input value.
     *
     * @param input The input value to perform the operation on.
     * @return The result after performing the operation.
     */
    fun runOperation(input: Double): Double {
        val returnable = when (operation) {
            Operand.MULTIPLY -> input * value
            Operand.DIVIDE -> input / value
            Operand.ADD -> input + value
            Operand.SUBTRACT -> input - value
        }
        return returnable
    }
}

enum class Operand {
    MULTIPLY,
    DIVIDE,
    ADD,
    SUBTRACT
}