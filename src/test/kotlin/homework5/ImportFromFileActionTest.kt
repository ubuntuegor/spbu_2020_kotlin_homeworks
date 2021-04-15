package homework5

import common.test.remapStdIn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.OutputStream

internal class ImportFromFileActionTest {
    private val testMap = HashMap<String, String>(HashCode)
    private val action = ImportFromFileAction

    @Test
    fun perform() {
        val inputFile = javaClass.getResource("importData.json").path
        remapStdIn("$inputFile\n", OutputStream.nullOutputStream())
        action.perform(testMap)
        assertEquals("testValue", testMap["test"])
    }
}
