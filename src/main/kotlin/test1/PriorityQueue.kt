package test1

class PriorityQueueElement<T, K : Comparable<K>>(val value: T, private val priority: K) :
    Comparable<PriorityQueueElement<T, K>> {
    override fun compareTo(other: PriorityQueueElement<T, K>) = priority.compareTo(other.priority)
}

class PriorityQueue<T, K : Comparable<K>> {
    private val queue = java.util.PriorityQueue<PriorityQueueElement<T, K>>()

    fun enqueue(element: T, priority: K) {
        queue.add(PriorityQueueElement(element, priority))
    }

    fun remove() {
        queue.poll() ?: throw NoSuchElementException("This queue is empty.")
    }

    fun peek() = queue.peek()?.value ?: throw NoSuchElementException("This queue is empty.")

    fun rool() = queue.poll()?.value ?: throw NoSuchElementException("This queue is empty.")

    override fun toString() = queue.toList().map { it.value }.toString()
}
