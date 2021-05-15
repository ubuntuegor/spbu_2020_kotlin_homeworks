package homework7

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

internal class MatrixTest {
    companion object {
        private fun matricesWithZeroes() = listOf(
            Arguments.of(
                Matrix(listOf()),
                Matrix(listOf(listOf())),
                Matrix(listOf())
            ),
            Arguments.of(
                Matrix(
                    listOf(
                        listOf(0, 0),
                        listOf(0, 0),
                        listOf(0, 0)
                    )
                ), Matrix(
                    listOf(
                        listOf(3, 1, 5),
                        listOf(2, -2, 8),
                        listOf(2, 2, 1)
                    )
                ), Matrix(
                    listOf(
                        listOf(0, 0),
                        listOf(0, 0),
                        listOf(0, 0)
                    )
                )
            )
        )

        private fun hardcodedMatrices() = listOf(
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
            ),
            Arguments.of(
                Matrix(
                    listOf(
                        listOf(-11, 6, -51),
                        listOf(55, -9, -7),
                        listOf(-10, 51, -139),
                        listOf(-123, -3, -52),
                        listOf(-41, 81, 110)
                    )
                ), Matrix(
                    listOf(
                        listOf(-4, 8, -7, 6, -3, 6),
                        listOf(8, -9, 2, -5, 9, -10),
                        listOf(10, -5, -8, 3, 0, 5),
                        listOf(1, -8, -7, -3, -2, 7),
                        listOf(-9, -5, 1, -3, -7, -9)
                    )
                ), Matrix(
                    listOf(
                        listOf(4, 4, -8),
                        listOf(3, -2, -2),
                        listOf(4, -2, 7),
                        listOf(9, 5, -1),
                        listOf(3, -10, 0),
                        listOf(-6, -6, -2)
                    )
                )
            ),
        )

        private fun randomMatrices(): List<Arguments> {
            val size = 100
            val matrix = Matrix(List(size) { List(size) { Random.nextInt() } })
            val identity = Matrix(List(size) { index1 -> List(size) { index2 -> if (index1 == index2) 1 else 0 } })
            return listOf(Arguments.of(matrix, matrix, identity))
        }

        @JvmStatic
        fun multiplicationData() =
            matricesWithZeroes() + hardcodedMatrices() + randomMatrices()
    }

    @Test
    fun improperSizes() {
        assertThrows<IllegalArgumentException> {
            Matrix(
                listOf(
                    listOf(1, 2, 3),
                    listOf(1, 2),
                    listOf(2, 3, 4, 1)
                )
            )
        }
    }

    @MethodSource("multiplicationData")
    @ParameterizedTest(name = "times - {index}")
    fun times(expected: Matrix, matrix1: Matrix, matrix2: Matrix) {
        assertEquals(expected, runBlocking { matrix1 * matrix2 })
    }

    @Test
    fun timesIllegalArgument() {
        val matrix1 = Matrix(
            listOf(
                listOf(1, 2, 3),
                listOf(3, 2, 1)
            )
        )
        val matrix2 = Matrix(
            listOf(
                listOf(1, 3),
                listOf(3, 1)
            )
        )
        assertThrows<IllegalArgumentException> { runBlocking { matrix1 * matrix2 } }
    }
}
