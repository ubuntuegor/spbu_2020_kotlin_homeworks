package homework6

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface ParallelRunnable : Runnable {
    fun runInParallel()
    fun join()
}

abstract class MergeRunnable<T : Comparable<T>>(
    private val list1: List<T>,
    private val list2: List<T>,
    private val recursionLimit: Int
) : ParallelRunnable {
    lateinit var result: List<T>
        private set

    abstract fun createInstance(list1: List<T>, list2: List<T>, recursionLimit: Int): MergeRunnable<T>

    private fun searchSplitPosition(list: List<T>, separator: T) =
        list.binarySearch(separator).let { if (it < 0) it.inv() else it }

    private fun parallelMerge(): List<T> {
        if (recursionLimit <= 0) return merge()

        val (leftList, rightList) = if (list1.size < list2.size) list2 to list1 else list1 to list2
        val result = mutableListOf<T>()

        return if (leftList.isEmpty()) result
        else {
            val leftListSplit = leftList.size / 2
            val rightListSplit = searchSplitPosition(rightList, leftList[leftListSplit])

            val leftHalvesRunnable = createInstance(
                leftList.subList(0, leftListSplit),
                rightList.subList(0, rightListSplit),
                recursionLimit - 1
            )
            val rightHalvesRunnable = createInstance(
                leftList.subList(leftListSplit + 1, leftList.size),
                rightList.subList(rightListSplit, rightList.size),
                recursionLimit - 1
            )

            leftHalvesRunnable.runInParallel()
            rightHalvesRunnable.run()
            leftHalvesRunnable.join()

            result.addAll(leftHalvesRunnable.result)
            result.add(leftList[leftListSplit])
            result.addAll(rightHalvesRunnable.result)

            result
        }
    }

    private fun merge(): List<T> {
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

    override fun run() {
        result = parallelMerge()
    }
}

class ThreadsMergeRunnable<T : Comparable<T>>(list1: List<T>, list2: List<T>, recursionLimit: Int) :
    MergeRunnable<T>(list1, list2, recursionLimit) {
    override fun createInstance(list1: List<T>, list2: List<T>, recursionLimit: Int) =
        ThreadsMergeRunnable(list1, list2, recursionLimit)

    private lateinit var thread: Thread

    override fun runInParallel() {
        thread = Thread(this)
        thread.start()
    }

    override fun join() {
        thread.join()
    }
}

class CoroutinesMergeRunnable<T : Comparable<T>>(list1: List<T>, list2: List<T>, recursionLimit: Int) :
    MergeRunnable<T>(list1, list2, recursionLimit) {
    override fun createInstance(list1: List<T>, list2: List<T>, recursionLimit: Int) =
        CoroutinesMergeRunnable(list1, list2, recursionLimit)

    private lateinit var job: Job

    override fun runInParallel() {
        job = GlobalScope.launch { this@CoroutinesMergeRunnable.run() }
    }

    override fun join() {
        runBlocking { job.join() }
    }
}

abstract class MergeSortRunnable<T : Comparable<T>>(
    private val list: List<T>,
    private val recursionLimit: Int
) : ParallelRunnable {
    lateinit var result: List<T>
        private set

    abstract fun createInstance(list: List<T>, recursionLimit: Int): MergeSortRunnable<T>
    abstract fun merge(list1: List<T>, list2: List<T>, recursionLimit: Int): MergeRunnable<T>

    private fun mergeSort(): List<T> {
        if (list.size <= 1) return list
        val mid = list.size / 2
        val leftHalfRunnable = createInstance(list.subList(0, mid), recursionLimit - 1)
        val rightHalfRunnable = createInstance(list.subList(mid, list.size), recursionLimit - 1)

        if (recursionLimit > 0) {
            leftHalfRunnable.runInParallel()
            rightHalfRunnable.run()
            leftHalfRunnable.join()
        } else {
            leftHalfRunnable.run()
            rightHalfRunnable.run()
        }

        return merge(
            leftHalfRunnable.result,
            rightHalfRunnable.result,
            recursionLimit
        ).also { it.run() }.result
    }

    override fun run() {
        result = mergeSort()
    }
}

class ThreadsMergeSortRunnable<T : Comparable<T>>(list: List<T>, recursionLimit: Int) :
    MergeSortRunnable<T>(list, recursionLimit) {
    override fun createInstance(list: List<T>, recursionLimit: Int) =
        ThreadsMergeSortRunnable(list, recursionLimit)

    override fun merge(list1: List<T>, list2: List<T>, recursionLimit: Int) =
        ThreadsMergeRunnable(list1, list2, recursionLimit)

    private lateinit var thread: Thread

    override fun runInParallel() {
        thread = Thread(this)
        thread.start()
    }

    override fun join() {
        thread.join()
    }
}

class CoroutinesMergeSortRunnable<T : Comparable<T>>(list: List<T>, recursionLimit: Int) :
    MergeSortRunnable<T>(list, recursionLimit) {
    override fun createInstance(list: List<T>, recursionLimit: Int) =
        CoroutinesMergeSortRunnable(list, recursionLimit)

    override fun merge(list1: List<T>, list2: List<T>, recursionLimit: Int) =
        CoroutinesMergeRunnable(list1, list2, recursionLimit)

    private lateinit var job: Job

    override fun runInParallel() {
        job = GlobalScope.launch { this@CoroutinesMergeSortRunnable.run() }
    }

    override fun join() {
        runBlocking { job.join() }
    }
}

class ThreadsMergeSorter<T : Comparable<T>>(private val recursionLimit: Int) : Sorter<T> {
    override fun sort(list: List<T>) =
        ThreadsMergeSortRunnable(list, recursionLimit).also { it.run() }.result
}

class CoroutinesMergeSorter<T : Comparable<T>>(private val recursionLimit: Int) : Sorter<T> {
    override fun sort(list: List<T>) =
        CoroutinesMergeSortRunnable(list, recursionLimit).also { it.run() }.result
}
