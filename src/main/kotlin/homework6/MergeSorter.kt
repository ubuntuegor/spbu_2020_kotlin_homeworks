package homework6

import java.util.concurrent.Semaphore

class MergeSortThread<T : Comparable<T>>(
    private val inputList: List<T>,
    private val semaphore: Semaphore
) : Thread() {
    private lateinit var _result: List<T>
    val result: List<T>
        get() = _result

    private fun merge(list1: List<T>, list2: List<T>): List<T> {
        val union = mutableListOf<T>()
        var index1 = 0
        var index2 = 0

        while (index1 < list1.size || index2 < list2.size) {
            when {
                index1 == list1.size -> union.add(list2[index2++])
                index2 == list2.size -> union.add(list1[index1++])
                list1[index1] <= list2[index2] -> union.add(list1[index1++])
                else -> union.add(list2[index2++])
            }
        }
        return union
    }

    private fun mergeSort(list: List<T>): List<T> {
        if (list.size <= 1) return list
        val mid = list.size / 2
        val leftHalfThread = MergeSortThread(list.subList(0, mid), semaphore)
        leftHalfThread.start()
        val rightHalf = mergeSort(list.subList(mid, list.size))
        leftHalfThread.join()

        semaphore.acquireUninterruptibly()
        val result = merge(leftHalfThread.result, rightHalf)
        semaphore.release()
        return result
    }

    override fun run() {
        _result = mergeSort(inputList)
    }
}

class MergeSorter<T : Comparable<T>>(threads: Int) {
    private val semaphore = Semaphore(threads)

    fun sort(list: List<T>): List<T> {
        val thread = MergeSortThread(list, semaphore)
        thread.start()
        thread.join()
        return thread.result
    }
}
