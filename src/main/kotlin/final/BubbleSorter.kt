package final

import common.error.exitWithError

/**
 * Can sort an iterable in a user-friendly way.
 */
class BubbleSorter<T>(private val comparator: Comparator<T>) {
    /**
     * Returns a list of all elements according to the comparator.
     */
    fun sort(iterable: Iterable<T>): List<T> {
        val list = iterable.toMutableList()
        var swap = true
        while (swap) {
            swap = false
            for (i in 1 until list.size) {
                val comparison = try {
                    comparator.compare(list[i], list[i - 1])
                } catch (e: IllegalArgumentException) {
                    exitWithError("Can't compare these two elements: ${list[i]}, ${list[i - 1]}")
                } catch (e: ArithmeticException) {
                    exitWithError(
                        "Can't compare these two elements due to arithmetic error: ${list[i]}, ${list[i - 1]}"
                    )
                }
                if (comparison < 0) {
                    list[i] = list[i - 1].also { list[i - 1] = list[i] }
                    swap = true
                }
            }
        }
        return list
    }
}
