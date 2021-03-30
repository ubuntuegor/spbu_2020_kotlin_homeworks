package homework1

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
    private val logStorage = PerformedCommandStorage()

    @Test
    fun performAction() {
        logStorage.performAction(AppendToStartAction(1))
        logStorage.performAction(AppendToStartAction(2))
        logStorage.performAction(AppendToStartAction(3))
        logStorage.performAction(AppendToStartAction(4))
        logStorage.performAction(AppendToEndAction(5))
        logStorage.performAction(MoveElementAction(0, 3))
        assertEquals(listOf(3, 2, 1, 4, 5), logStorage.storage)
    }

    @Test
    fun undo() {
        logStorage.performAction(AppendToStartAction(1))
        logStorage.performAction(AppendToStartAction(2))
        logStorage.performAction(AppendToStartAction(3))
        repeat(2) { logStorage.undoLastAction() }
        assertEquals(listOf(1), logStorage.storage)
    }

    @Test
    fun undoEmpty() {
        assertThrows<NoSuchElementException> { logStorage.undoLastAction() }
    }

    @Test
    fun loadFromJson() {
        logStorage.loadFromJson(javaClass.getResource("savedActionsLoad.json").path)
        assertEquals(listOf(3, 34, 4, 1, 1), logStorage.storage)
    }

    @Test
    fun saveEmptyToJson(@TempDir tempDir: Path) {
        val tempFile = tempDir.resolve("savedActionsEmpty.json").toString()
        logStorage.saveToJson(tempFile)
        assertEquals("[]", File(tempFile).readText())
    }

    @Test
    fun saveToJson(@TempDir tempDir: Path) {
        val tempFile = tempDir.resolve("savedActionsSave.json").toString()
        logStorage.performAction(AppendToStartAction(1))
        logStorage.performAction(AppendToStartAction(2))
        logStorage.performAction(AppendToEndAction(5))
        logStorage.saveToJson(tempFile)
        assertEquals(javaClass.getResource("savedActionsSave.json").readText(), File(tempFile).readText())
    }
}
