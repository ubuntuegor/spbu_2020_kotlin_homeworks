package homework1

import homework1.actions.AppendToEndAction
import homework1.actions.AppendToStartAction
import homework1.actions.MoveElementAction
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

internal class PerformedCommandStorageTest {
    @Test
    fun performActionAndUndo() {
        val logStorage = PerformedCommandStorage()
        logStorage.performAction(AppendToStartAction(1))
        logStorage.performAction(AppendToStartAction(2))
        logStorage.performAction(AppendToStartAction(3))
        logStorage.performAction(AppendToStartAction(4))
        assertEquals(listOf(4, 3, 2, 1), logStorage.storage)
        logStorage.performAction(AppendToEndAction(5))
        assertEquals(listOf(4, 3, 2, 1, 5), logStorage.storage)
        logStorage.performAction(MoveElementAction(0, 3))
        assertEquals(listOf(3, 2, 1, 4, 5), logStorage.storage)
        repeat(4) { logStorage.undoLastAction() }
        assertEquals(listOf(2, 1), logStorage.storage)
    }

    @Test
    fun loadFromJson() {
        val logStorage = PerformedCommandStorage()
        logStorage.loadFromJson(javaClass.getResource("savedActionsLoad.json").path)
        assertEquals(listOf(3, 34, 4, 1, 1), logStorage.storage)
    }

    @Test
    fun saveToJson() {
        val logStorage = PerformedCommandStorage()
        logStorage.saveToJson("temp.json")
        assertEquals("[]", File("temp.json").readText())
        logStorage.performAction(AppendToStartAction(1))
        logStorage.performAction(AppendToStartAction(2))
        logStorage.performAction(AppendToEndAction(5))
        logStorage.saveToJson("temp.json")
        assertEquals(javaClass.getResource("savedActionsSave.json").readText(), File("temp.json").readText())
        File("temp.json").delete()
    }
}
