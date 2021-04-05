package homework5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ExpressionTreeTest {
    companion object {
        @JvmStatic
        fun parseData() = listOf(
            Arguments.of("(/ (- (* 6 2) (+ 7 2)) 2)", "(/(-(* 6 2)(+ 7 2))2)"),
            Arguments.of("(+ (* 2 2) 2)", " (+   (* 2  2) 2 )")
        )
        @JvmStatic
        fun expressionTreeData() = listOf(
            Arguments.of(1, "(/ (- (* 6 2) (+ 7 2)) 2)"),
            Arguments.of(6, "(+ (* 2 2) 2)"),
            Arguments.of(4, "(* (+ 1 1) 2)")
        )
    }

    @MethodSource("parseData")
    @ParameterizedTest
    fun parse(expected: String, input: String) {
        val tree = ExpressionTree.parse(input)
        assertEquals(expected, tree.toString())
    }

    @MethodSource("expressionTreeData")
    @ParameterizedTest
    fun calculateValue(expectedValue: Int, input: String) {
        val tree = ExpressionTree.parse(input)
        assertEquals(expectedValue, tree.calculateValue())
    }
}
