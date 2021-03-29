package homework1.actions

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

internal class AppendToEndActionTest {
    @Test
    fun perform() {
        val list = mutableListOf(1, 2)
        AppendToEndAction(3).perform(list)
        assertEquals(mutableListOf(1, 2, 3), list)
    }

    @Test
    fun undo() {
        val list = mutableListOf(1, 2, 3)
        AppendToEndAction(3).undo(list)
        assertEquals(mutableListOf(1, 2), list)
    }
}
