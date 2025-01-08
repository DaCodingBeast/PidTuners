package com.dacodingbeast.pidtuners

import com.dacodingbeast.pidtuners.HardwareSetup.AnalogEncoderCalculator
import com.dacodingbeast.pidtuners.HardwareSetup.Operand
import com.dacodingbeast.pidtuners.HardwareSetup.Operation
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AnalogInputTest {
    @Test
    fun testGetCurrentPosition() {
        assertEquals(AnalogEncoderCalculator(listOf(Operation(Operand.MULTIPLY,5.0))).runOperations(1.0),5.0)
        assertEquals(AnalogEncoderCalculator(listOf(Operation(Operand.DIVIDE,5.0))).runOperations(5.0),1.0)
    }
    @Test
fun testMultipleOperations() {
    val operations = listOf(
        Operation(Operand.MULTIPLY, 2.0),
        Operation(Operand.ADD, 3.0),
        Operation(Operand.SUBTRACT, 1.0),
        Operation(Operand.DIVIDE, 2.0)
    )
    val calculator = AnalogEncoderCalculator(operations)
    val result = calculator.runOperations(5.0)
    assertEquals(6.0, result)
}
}