package homework1.actions

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

internal class MoveElementActionTest {
    @Test
    fun perform() {
        val list = mutableListOf(1, 2, 3, 4)
        MoveElementAction(1, 2).perform(list)
        assertEquals(mutableListOf(1, 3, 2, 4), list)
    }

    @Test
    fun undo() {
        val list = mutableListOf(1, 3, 2, 4)
        MoveElementAction(1, 2).undo(list)
        assertEquals(mutableListOf(1, 2, 3, 4), list)
    }
}
