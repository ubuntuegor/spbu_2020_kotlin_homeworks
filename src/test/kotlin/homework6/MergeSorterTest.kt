package homework6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class MergeSorterTest {
    companion object {
        @JvmStatic
        fun sortData() = listOf(
            Arguments.of(listOf(1, 2, 3, 4), listOf(4, 3, 2, 1)),
            Arguments.of(listOf('a', 'b', 'c'), listOf('b', 'c', 'a')),
            Arguments.of(listOf<Int>(), listOf<Int>()),
        )
    }

    @MethodSource("sortData")
    @ParameterizedTest
    fun <T : Comparable<Any>> sort(expected: List<T>, unsorted: List<T>) {
        val sorter = MergeSorter<T>(10, 10, true)
        assertEquals(expected, sorter.sort(unsorted))
    }

    @MethodSource("sortData")
    @ParameterizedTest
    fun <T : Comparable<Any>> sortSync(expected: List<T>, unsorted: List<T>) {
        val sorter = MergeSorter<T>(0, 1, true)
        assertEquals(expected, sorter.sort(unsorted))
    }
}
