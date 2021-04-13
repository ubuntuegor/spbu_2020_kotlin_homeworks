package homework5

import common.test.remapStdIn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.OutputStream

internal class ChangeHashFunctionActionTest {
    private val testMap = HashMap<String, String>(HashCode())
    private val action = ChangeHashFunctionAction(testMap)
    private val stats: Map<String, Any>
        get() = testMap.getStatistics()

    init {
        testMap["1"] = "1"
        testMap["3"] = "3"
    }

    @Test
    fun beforePerformConflicts() =
        assertEquals(0, stats["Conflict count"])

    @Test
    fun beforePerformBucketSize() =
        assertEquals(1, stats["Maximum bucket size"])

    @Test
    fun performConflicts() {
        remapStdIn("2\n", OutputStream.nullOutputStream())
        action.perform()
        assertEquals(1, stats["Conflict count"])
    }

    @Test
    fun performBucketSize() {
        remapStdIn("2\n", OutputStream.nullOutputStream())
        action.perform()
        assertEquals(2, stats["Maximum bucket size"])
    }
}
