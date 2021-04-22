package homework6

import java.util.concurrent.Semaphore

class MergeRunnable<T : Comparable<T>>(
    private val list1: List<T>,
    private val list2: List<T>,
    private val recursionLimit: Int,
    private val options: MergeSorterOptions
) : Runnable {
    private lateinit var _result: List<T>
    val result: List<T>
        get() = _result

    private fun searchSplitPosition(list: List<T>, separator: T) =
        list.binarySearch(separator).let { if (it < 0) it.inv() else it }

    private fun parallelMerge(): List<T> {
        if (recursionLimit <= 0) return merge()

        val leftList = if (list1.size < list2.size) list2 else list1
        val rightList = if (list1.size < list2.size) list1 else list2
        val result = mutableListOf<T>()

        return if (leftList.isEmpty()) result
        else {
            options.semaphore.acquireUninterruptibly()
            val leftListSplit = leftList.size / 2
            val rightListSplit = searchSplitPosition(rightList, leftList[leftListSplit])
            options.semaphore.release()

            val leftHalvesRunnable = MergeRunnable(
                leftList.subList(0, leftListSplit),
                rightList.subList(0, rightListSplit),
                recursionLimit - 1,
                options
            )
            val rightHalvesRunnable = MergeRunnable(
                leftList.subList(leftListSplit + 1, leftList.size),
                rightList.subList(rightListSplit, rightList.size),
                recursionLimit - 1,
                options
            )

            val leftHalvesThread = Thread(leftHalvesRunnable)
            leftHalvesThread.start()
            rightHalvesRunnable.run()
            leftHalvesThread.join()

            result.addAll(leftHalvesRunnable.result)
            result.add(leftList[leftListSplit])
            result.addAll(rightHalvesRunnable.result)

            result
        }
    }

    private fun merge(): List<T> {
        options.semaphore.acquireUninterruptibly()
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
        options.semaphore.release()
        return union
    }

    override fun run() {
        _result = if (options.useParallelMerge) parallelMerge() else merge()
    }
}

class MergeSortRunnable<T : Comparable<T>>(
    private val list: List<T>,
    private val recursionLimit: Int,
    private val options: MergeSorterOptions
) : Runnable {
    private lateinit var _result: List<T>
    val result: List<T>
        get() = _result

    private fun mergeSort(): List<T> {
        if (list.size <= 1) return list
        val mid = list.size / 2
        val leftHalfRunnable = MergeSortRunnable(list.subList(0, mid), recursionLimit - 1, options)
        val rightHalfRunnable = MergeSortRunnable(list.subList(mid, list.size), recursionLimit - 1, options)

        if (recursionLimit > 0) {
            val leftHalfThread = Thread(leftHalfRunnable)
            leftHalfThread.start()
            rightHalfRunnable.run()
            leftHalfThread.join()
        } else {
            leftHalfRunnable.run()
            rightHalfRunnable.run()
        }

        return MergeRunnable(
            leftHalfRunnable.result,
            rightHalfRunnable.result,
            recursionLimit,
            options
        ).also { it.run() }.result
    }

    override fun run() {
        _result = mergeSort()
    }
}

data class MergeSorterOptions(
    val useParallelMerge: Boolean,
    val semaphore: Semaphore
)

class MergeSorter<T : Comparable<T>>(
    private val recursionLimit: Int,
    workingThreads: Int,
    useParallelMerge: Boolean
) {
    private val options = MergeSorterOptions(useParallelMerge, Semaphore(workingThreads))

    fun sort(list: List<T>) =
        MergeSortRunnable(list, recursionLimit, options).also { it.run() }.result
}
