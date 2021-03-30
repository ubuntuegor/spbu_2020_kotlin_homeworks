package test1

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

internal class PriorityQueueTest {
    private val queue = PriorityQueue<String, Int>()

    @Test
    fun enqueue() {
        queue.enqueue("Second element", 3)
        queue.enqueue("First element", 1)
        assertEquals(queue.peek(), "First element")
    }

    @Test
    fun removeEmpty() {
        assertThrows<NoSuchElementException> { queue.remove() }
    }

    @Test
    fun remove() {
        queue.enqueue("Second element", 3)
        queue.enqueue("First element", 1)
        queue.remove()
        assertEquals(queue.peek(), "Second element")
    }

    @Test
    fun peekEmpty() {
        assertThrows<NoSuchElementException> { queue.peek() }
    }

    @Test
    fun peek() {
        queue.enqueue("First element", 1)
        assertEquals(queue.peek(), "First element")
    }

    @Test
    fun roolEmpty() {
        assertThrows<NoSuchElementException> { queue.rool() }
    }

    @Test
    fun rool() {
        queue.enqueue("Second element", 3)
        queue.enqueue("First element", 1)
        assertEquals(queue.rool(), "First element")
        assertEquals(queue.peek(), "Second element")
    }
}
