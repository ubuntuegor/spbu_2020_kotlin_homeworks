package final

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

internal class BubbleSorterTest {
    companion object {
        @JvmStatic
        fun numbers(): List<Arguments> {
            val arguments = mutableListOf<Arguments>()
            arguments.add(Arguments.of(listOf<Int>(), listOf<Int>()))
            arguments.add(Arguments.of(listOf(1, 2, 3, 4), listOf(4, 3, 2, 1)))
            val arbitraryList = List(100) { Random.nextInt() }
            arguments.add(Arguments.of(arbitraryList.sorted(), arbitraryList))
            return arguments
        }

        @JvmStatic
        fun strings(): List<Arguments> {
            val arguments = mutableListOf<Arguments>()
            val testData =
                ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
                        "labore et dolore magna aliqua.").split(" ")
            arguments.add(Arguments.of(testData.sorted(), testData))
            return arguments
        }

        @JvmStatic
        fun sortData() = numbers() + strings()
    }

    @MethodSource("sortData")
    @ParameterizedTest(name = "sort - {index}")
    fun <T : Comparable<T>> sort(expected: List<T>, list: List<T>) {
        assertEquals(expected, BubbleSorter(Comparator.naturalOrder<T>()).sort(list))
    }

    @Test
    fun sortNullsFirst() {
        val list = listOf(1, 4, 2, null, 3)
        assertEquals(
            listOf(null, 1, 2, 3, 4),
            BubbleSorter(Comparator.nullsFirst(Comparator.naturalOrder<Int>())).sort(list)
        )
    }
}
