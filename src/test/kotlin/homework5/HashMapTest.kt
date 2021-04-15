package homework5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

internal class HashMapTest {
    private val emptyMap = HashMap<String, String>(HashCode)
    private val testDataMap = HashMap<String, String>(RollingHash)

    init {
        testDataMap["em"] = "me"
        testDataMap["gec"] = "gec"
        testDataMap["_"] = "Data"
        testDataMap["6"] = "42"
        testDataMap["test"] = "test"
        for (i in 'a'..'z') testDataMap[i.toString()] = i.toString()
    }

    @Test
    fun get() {
        assertEquals("Data", testDataMap["_"])
    }

    @Test
    fun getNonExistent() {
        assertNull(testDataMap["test2"])
    }

    @Test
    fun set() {
        emptyMap["lol"] = "1"
        assertEquals("1", emptyMap["lol"])
    }

    @Test
    fun setExisting() {
        testDataMap["gec"] = "hey"
        assertEquals("hey", testDataMap["gec"])
    }

    @Test
    fun remove() {
        testDataMap.remove("em")
        assertNull(testDataMap["em"])
    }

    @Test
    fun getStatisticsSize() {
        assertEquals(31, testDataMap.getStatistics()["Total elements"])
    }

    @Test
    fun getStatisticsBucketCount() {
        assertEquals(64, testDataMap.getStatistics()["Total buckets"])
    }

    @Test
    fun getStatisticsConflictCount() {
        assertEquals(2, testDataMap.getStatistics()["Maximum bucket size"])
    }
}
