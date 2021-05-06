package homework6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

internal class MergeSorterTest {
    companion object {
        @JvmStatic
        fun sortData(): List<Arguments> {
            val arguments = mutableListOf<Arguments>()
            val largeRandomList = List(100_000) { Random.nextInt() }
            val sortedRandomList = largeRandomList.sorted()
            for (recursionLimit in (0..6)) {
                arguments.add(Arguments.of(listOf(1, 2, 3, 4), listOf(4, 3, 2, 1), recursionLimit))
                arguments.add(Arguments.of(listOf(1, 2, 2, 2, 3, 4), listOf(4, 2, 3, 2, 1, 2), recursionLimit))
                arguments.add(Arguments.of(listOf('a', 'b', 'c'), listOf('b', 'c', 'a'), recursionLimit))
                arguments.add(Arguments.of(listOf<Int>(), listOf<Int>(), recursionLimit))
                arguments.add(Arguments.of((1..100_000).toList(), (1..100_000).shuffled(), recursionLimit))
                arguments.add(Arguments.of(sortedRandomList, largeRandomList, recursionLimit))
            }
            return arguments
        }
    }

    @MethodSource("sortData")
    @ParameterizedTest(name = "sort - {0} (recursion limit {2})")
    fun <T : Comparable<Any>> sort(expected: List<T>, unsorted: List<T>, recursionLimit: Int) {
        val sorter = MergeSorter<T>(recursionLimit, true)
        assertEquals(expected, sorter.sort(unsorted))
    }
}
