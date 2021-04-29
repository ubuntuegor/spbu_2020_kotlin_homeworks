package homework1

import homework1.PerformedCommandStorage.IntJson.loadFromJson
import homework1.PerformedCommandStorage.IntJson.saveToJson
import homework1.actions.AppendToEndAction
import homework1.actions.AppendToStartAction
import homework1.actions.MoveElementAction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

internal class PerformedCommandStorageTest {
    private val logIntStorage = PerformedCommandStorage<Int>()
    private val logStringStorage = PerformedCommandStorage<String>()
    private val logNumberStorage = PerformedCommandStorage<Number>()

    @Test
    fun performAction() {
        logIntStorage.performAction(AppendToStartAction(1))
        logIntStorage.performAction(AppendToStartAction(2))
        logIntStorage.performAction(AppendToStartAction(3))
        logIntStorage.performAction(AppendToStartAction(4))
        logIntStorage.performAction(AppendToEndAction(5))
        logIntStorage.performAction(MoveElementAction(0, 3))
        assertEquals(listOf(3, 2, 1, 4, 5), logIntStorage.storage)
    }

    @Test
    fun performActionString() {
        logStringStorage.performAction(AppendToEndAction("hi"))
        logStringStorage.performAction(AppendToStartAction("hello"))
        assertEquals(listOf("hello", "hi"), logStringStorage.storage)
    }

    @Test
    fun performActionNumber() {
        logNumberStorage.performAction(AppendToEndAction(11))
        logNumberStorage.performAction(AppendToEndAction(3.25))
        logNumberStorage.performAction(AppendToEndAction(Long.MAX_VALUE))
        assertEquals(listOf<Number>(11, 3.25, Long.MAX_VALUE), logNumberStorage.storage)
    }

    @Test
    fun undo() {
        logIntStorage.performAction(AppendToStartAction(1))
        logIntStorage.performAction(AppendToStartAction(2))
        logIntStorage.performAction(AppendToStartAction(3))
        repeat(2) { logIntStorage.undoLastAction() }
        assertEquals(listOf(1), logIntStorage.storage)
    }

    @Test
    fun undoEmpty() {
        assertThrows<NoSuchElementException> { logIntStorage.undoLastAction() }
    }

    @Test
    fun loadFromJson() {
        logIntStorage.loadFromJson(javaClass.getResource("savedActionsLoad.json").path)
        assertEquals(listOf(3, 34, 4, 1, 1), logIntStorage.storage)
    }

    @Test
    fun saveEmptyToJson(@TempDir tempDir: Path) {
        val tempFile = tempDir.resolve("savedActionsEmpty.json").toString()
        logIntStorage.saveToJson(tempFile)
        assertEquals("[]", File(tempFile).readText())
    }

    @Test
    fun saveToJson(@TempDir tempDir: Path) {
        val tempFile = tempDir.resolve("savedActionsSave.json").toString()
        logIntStorage.performAction(AppendToStartAction(1))
        logIntStorage.performAction(AppendToStartAction(2))
        logIntStorage.performAction(AppendToEndAction(5))
        logIntStorage.saveToJson(tempFile)
        assertEquals(javaClass.getResource("savedActionsSave.json").readText(), File(tempFile).readText())
    }
}
