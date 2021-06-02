package test1

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class VectorTest {
    companion object {
        data class VectorData<T : ArithmeticAvailable<T>>(
            val vector1: Vector<T>,
            val vector2: Vector<T>,
            val sum: Vector<T>,
            val difference: Vector<T>,
            val scalar: T
        )

        @JvmStatic
        fun vectorData() = listOf(
            Arguments.of(
                VectorData(
                    vector1 = Vector.fromInts(22, 34, 5),
                    vector2 = Vector.fromInts(-10, 2, 19),
                    sum = Vector.fromInts(12, 36, 24),
                    difference = Vector.fromInts(32, 32, -14),
                    scalar = ArithmeticInt(-57)
                )
            ),
            Arguments.of(
                VectorData(
                    vector1 = Vector.fromInts(0, 23, -12, -32, 23),
                    vector2 = Vector.fromInts(-17, 15, 1, 21, 0),
                    sum = Vector.fromInts(-17, 38, -11, -11, 23),
                    difference = Vector.fromInts(17, 8, -13, -53, 23),
                    scalar = ArithmeticInt(-339)
                )
            )
        )

        @JvmStatic
        fun wrongSizes() = listOf(Arguments.of(Vector.fromInts(3, 2, 1), Vector.fromInts(1, 2)))
    }

    @Test
    fun getSize() {
        val vector = Vector.fromInts(1, 2, 3)
        assertEquals(3, vector.size)
    }

    @MethodSource("vectorData")
    @ParameterizedTest(name = "plus - {index}")
    fun <T : ArithmeticAvailable<T>> plus(data: VectorData<T>) {
        assertEquals(data.sum, data.vector1 + data.vector2)
    }

    @MethodSource("vectorData")
    @ParameterizedTest(name = "minus - {index}")
    fun <T : ArithmeticAvailable<T>> minus(data: VectorData<T>) {
        assertEquals(data.difference, data.vector1 - data.vector2)
    }

    @MethodSource("vectorData")
    @ParameterizedTest(name = "times - {index}")
    fun <T : ArithmeticAvailable<T>> times(data: VectorData<T>) {
        assertEquals(data.scalar, data.vector1 * data.vector2)
    }

    @MethodSource("wrongSizes")
    @ParameterizedTest(name = "plusWrong - {index}")
    fun <T : ArithmeticAvailable<T>> plusWrong(vector1: Vector<T>, vector2: Vector<T>) {
        assertThrows<IllegalArgumentException> { vector1 + vector2 }
    }

    @MethodSource("wrongSizes")
    @ParameterizedTest(name = "minusWrong - {index}")
    fun <T : ArithmeticAvailable<T>> minusWrong(vector1: Vector<T>, vector2: Vector<T>) {
        assertThrows<IllegalArgumentException> { vector1 - vector2 }
    }

    @MethodSource("wrongSizes")
    @ParameterizedTest(name = "timesWrong - {index}")
    fun <T : ArithmeticAvailable<T>> timesWrong(vector1: Vector<T>, vector2: Vector<T>) {
        assertThrows<IllegalArgumentException> { vector1 * vector2 }
    }

    @Test
    fun isNull() {
        assertEquals(true, Vector.fromInts(0, 0, 0).isNull())
    }
}
