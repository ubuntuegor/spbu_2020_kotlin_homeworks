package homework4

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class AvlMapTest {
    private val testMap = AvlMap<Int, String>()

    companion object {
        val randomData = mapOf(42 to "answer", 34 to "z", -1 to "n")
        @JvmStatic
        fun sizeData() = listOf(
            Arguments.of(0, mapOf<Int, String>()),
            Arguments.of(2, mapOf(1 to "one", 2 to "two"))
        )
        @JvmStatic
        fun containsData() = listOf(
            Arguments.of(true, Pair(34, "a"), mapOf(34 to "a")),
            Arguments.of(false, Pair(-34, "s"), mapOf(34 to "a")),
        )
    }

    @MethodSource("sizeData")
    @ParameterizedTest
    fun getSize(expectedSize: Int, sourceMap: Map<Int, String>) {
        testMap.putAll(sourceMap)
        assertEquals(expectedSize, testMap.size)
    }

    @MethodSource("containsData")
    @ParameterizedTest
    fun containsKey(expected: Boolean, pair: Pair<Int, String>, sourceMap: Map<Int, String>) {
        testMap.putAll(sourceMap)
        assertEquals(expected, testMap.containsKey(pair.first))
    }

    @MethodSource("containsData")
    @ParameterizedTest
    fun containsValue(expected: Boolean, pair: Pair<Int, String>, sourceMap: Map<Int, String>) {
        testMap.putAll(sourceMap)
        assertEquals(expected, testMap.containsValue(pair.second))
    }

    @Test
    fun get() {
        testMap[34] = "a"
        assertEquals("a", testMap[34])
    }

    @Test
    fun isEmpty() {
        assertEquals(true, testMap.isEmpty())
    }

    @Test
    fun isNotEmpty() {
        testMap.putAll(randomData)
        assertEquals(false, testMap.isEmpty())
    }

    @Test
    fun getEntries() {
        testMap[1] = "one"
        testMap[2] = "two"
        assertEquals(mutableMapOf(1 to "one", 2 to "two").entries, testMap.entries)
    }

    @Test
    fun getKeys() {
        testMap[1] = "one"
        testMap[2] = "two"
        assertEquals(mutableSetOf(1, 2), testMap.keys)
    }

    @Test
    fun getValues() {
        testMap[1] = "one"
        testMap[2] = "two"
        assertEquals(mutableListOf("one", "two"), testMap.values)
    }

    @Test
    fun clear() {
        testMap.putAll(randomData)
        testMap.clear()
        assertEquals(mutableSetOf<MutableMap.MutableEntry<Int, String>>(), testMap.entries)
    }

    @Test
    fun put() {
        testMap[34] = "a"
        assertEquals(true, testMap.containsKey(34))
    }

    @Test
    fun putAll() {
        testMap.putAll(randomData)
        assertEquals(randomData.entries, testMap.entries)
    }

    @Test
    fun remove() {
        testMap[1] = "one"
        testMap[2] = "two"
        testMap.remove(1)
        assertEquals(false, testMap.containsKey(1))
    }
}
