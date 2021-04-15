package homework5

import common.test.remapStdIn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.OutputStream

internal class ChangeHashFunctionActionTest {
    private val testMap = HashMap<String, String>(HashCode)
    private val action = ChangeHashFunctionAction
    private val stats: Map<String, Any>
        get() = testMap.getStatistics()

    init {
        testMap["ab"] = "1"
        testMap["bc"] = "3"
    }

    @Test
    fun beforePerformConflicts() =
        assertEquals(1, stats["Conflict count"])

    @Test
    fun beforePerformBucketSize() =
        assertEquals(2, stats["Maximum bucket size"])

    @Test
    fun performConflicts() {
        remapStdIn("1\n", OutputStream.nullOutputStream())
        action.perform(testMap)
        assertEquals(0, stats["Conflict count"])
    }

    @Test
    fun performBucketSize() {
        remapStdIn("1\n", OutputStream.nullOutputStream())
        action.perform(testMap)
        assertEquals(1, stats["Maximum bucket size"])
    }
}
