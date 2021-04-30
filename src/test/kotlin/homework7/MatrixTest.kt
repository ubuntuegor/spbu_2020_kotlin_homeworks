package homework7

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class MatrixTest {
    companion object {
        @JvmStatic
        fun multiplicationData() = listOf(
            Arguments.of(
                Matrix(listOf()),
                Matrix(listOf()),
                Matrix(listOf())
            ),
            Arguments.of(
                Matrix(
                    listOf(
                        listOf(15, 40),
                        listOf(36, 44),
                        listOf(-3, 18)
                    )
                ), Matrix(
                    listOf(
                        listOf(3, 1, 5),
                        listOf(2, -2, 8),
                        listOf(2, 2, 1)
                    )
                ), Matrix(
                    listOf(
                        listOf(-3, 2),
                        listOf(-1, 4),
                        listOf(5, 6)
                    )
                )
            )
        )
    }

    @MethodSource("multiplicationData")
    @ParameterizedTest(name = "times - {index}")
    fun times(expected: Matrix, matrix1: Matrix, matrix2: Matrix) {
        assertEquals(expected, runBlocking { matrix1 * matrix2 })
    }
}
