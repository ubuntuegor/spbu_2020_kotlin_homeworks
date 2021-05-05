package homework6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class MergeRunnableTest {
    companion object {
        @JvmStatic
        fun mergeData(): List<Arguments> {
            val arguments = mutableListOf<Arguments>()
            for (recursionLimit in (0..6)) {
                arguments.add(Arguments.of(listOf(1, 2, 3, 4), listOf(1, 3), listOf(2, 4), recursionLimit))
                arguments.add(Arguments.of(listOf(1, 1, 1, 1, 4), listOf(1, 4), listOf(1, 1, 1), recursionLimit))
                arguments.add(Arguments.of(listOf(2, 3, 4), listOf(2, 3, 4), listOf<Int>(), recursionLimit))
                arguments.add(Arguments.of(listOf<Int>(), listOf<Int>(), listOf<Int>(), recursionLimit))
                arguments.add(
                    Arguments.of(
                        (1..100_000).toList(),
                        (1..99_999 step 2).toList(),
                        (2..100_000 step 2).toList(),
                        recursionLimit
                    )
                )
            }
            return arguments
        }
    }

    @MethodSource("mergeData")
    @ParameterizedTest(name = "run - {0} (recursion limit {3})")
    fun run(expected: List<Int>, list1: List<Int>, list2: List<Int>, recursionLimit: Int) {
        val mergeRunnable = MergeRunnable(list1, list2, recursionLimit)
        mergeRunnable.run()
        assertEquals(expected, mergeRunnable.result)
    }
}
