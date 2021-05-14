package homework6

class MergeRunnable<T : Comparable<T>>(
    private val list1: List<T>,
    private val list2: List<T>,
    private val recursionLimit: Int
) : Runnable {
    lateinit var result: List<T>
        private set

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

            val leftHalvesRunnable = MergeRunnable(
                leftList.subList(0, leftListSplit),
                rightList.subList(0, rightListSplit),
                recursionLimit - 1
            )
            val rightHalvesRunnable = MergeRunnable(
                leftList.subList(leftListSplit + 1, leftList.size),
                rightList.subList(rightListSplit, rightList.size),
                recursionLimit - 1
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

class MergeSortRunnable<T : Comparable<T>>(
    private val list: List<T>,
    private val recursionLimit: Int
) : Runnable {
    lateinit var result: List<T>
        private set

    private fun mergeSort(): List<T> {
        if (list.size <= 1) return list
        val mid = list.size / 2
        val leftHalfRunnable = MergeSortRunnable(list.subList(0, mid), recursionLimit - 1)
        val rightHalfRunnable = MergeSortRunnable(list.subList(mid, list.size), recursionLimit - 1)

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
            recursionLimit
        ).also { it.run() }.result
    }

    override fun run() {
        result = mergeSort()
    }
}

class MergeSorter<T : Comparable<T>>(private val recursionLimit: Int) : Sorter<T> {
    override fun sort(list: List<T>) =
        MergeSortRunnable(list, recursionLimit).also { it.run() }.result
}
