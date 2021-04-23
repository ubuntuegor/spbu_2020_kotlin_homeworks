package homework6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.Semaphore

internal class MergeRunnableTest {
    companion object {
        @JvmStatic
        fun mergeData() = listOf(
            Arguments.of(listOf(1, 2, 3, 4), listOf(1, 3), listOf(2, 4)),
            Arguments.of(listOf(2, 3, 4), listOf(2, 3, 4), listOf<Int>()),
        )
    }

    @MethodSource("mergeData")
    @ParameterizedTest
    fun runSync(expected: List<Int>, list1: List<Int>, list2: List<Int>) {
        val options = MergeSorterOptions(false, Semaphore(10))
        val mergeRunnable = MergeRunnable(list1, list2, 0, options)
        mergeRunnable.run()
        assertEquals(expected, mergeRunnable.result)
    }

    @MethodSource("mergeData")
    @ParameterizedTest
    fun runParallel(expected: List<Int>, list1: List<Int>, list2: List<Int>) {
        val options = MergeSorterOptions(true, Semaphore(10))
        val mergeRunnable = MergeRunnable(list1, list2, 10, options)
        mergeRunnable.run()
        assertEquals(expected, mergeRunnable.result)
    }
}
