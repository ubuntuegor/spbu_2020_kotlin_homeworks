package homework1.actions

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

internal class AppendToStartActionTest {
    @Test
    fun perform() {
        val list = mutableListOf(1, 2)
        AppendToStartAction(3).perform(list)
        assertEquals(mutableListOf(3, 1, 2), list)
    }

    @Test
    fun undo() {
        val list = mutableListOf(3, 1, 2)
        AppendToStartAction(3).undo(list)
        assertEquals(mutableListOf(1, 2), list)
    }
}
