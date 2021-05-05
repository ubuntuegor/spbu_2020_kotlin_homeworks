package homework6

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.math.max

class CoroutinesMergeSorter<T : Comparable<T>>(
    private val recursionLimit: Int,
    private val useParallelMerge: Boolean
) : Sorter<T> {
    private fun searchSplitPosition(list: List<T>, separator: T) =
        list.binarySearch(separator).let { if (it < 0) it.inv() else it }

    private suspend fun parallelMerge(list1: List<T>, list2: List<T>, recursionLimit: Int): List<T> = coroutineScope {
        when {
            recursionLimit <= 0 -> syncMerge(list1, list2)
            max(list1.size, list2.size) == 0 -> listOf()
            else -> {
                val (leftList, rightList) = if (list1.size < list2.size) list2 to list1 else list1 to list2
                val result = mutableListOf<T>()

                val leftListSplit = leftList.size / 2
                val rightListSplit = searchSplitPosition(rightList, leftList[leftListSplit])

                val leftHalfCoroutine = async {
                    parallelMerge(
                        leftList.subList(0, leftListSplit),
                        rightList.subList(0, rightListSplit),
                        recursionLimit - 1
                    )
                }
                val rightHalf = parallelMerge(
                    leftList.subList(leftListSplit + 1, leftList.size),
                    rightList.subList(rightListSplit, rightList.size),
                    recursionLimit - 1
                )
                val leftHalf = leftHalfCoroutine.await()

                result.addAll(leftHalf)
                result.add(leftList[leftListSplit])
                result.addAll(rightHalf)

                result
            }
        }
    }

    private fun syncMerge(list1: List<T>, list2: List<T>): List<T> {
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

    private fun merge(list1: List<T>, list2: List<T>, recursionLimit: Int) =
        if (useParallelMerge) runBlocking { parallelMerge(list1, list2, recursionLimit) }
        else syncMerge(list1, list2)

    private suspend fun mergeSort(list: List<T>, recursionLimit: Int): List<T> = coroutineScope {
        if (list.size <= 1) list
        else {
            val mid = list.size / 2

            if (recursionLimit > 0) {
                val leftHalfCoroutine = async { mergeSort(list.subList(0, mid), recursionLimit - 1) }
                val rightHalf = mergeSort(list.subList(mid, list.size), recursionLimit - 1)
                val leftHalf = leftHalfCoroutine.await()
                merge(leftHalf, rightHalf, recursionLimit)
            } else {
                val leftHalf = mergeSort(list.subList(0, mid), recursionLimit - 1)
                val rightHalf = mergeSort(list.subList(mid, list.size), recursionLimit - 1)
                merge(leftHalf, rightHalf, recursionLimit)
            }
        }
    }

    override fun sort(list: List<T>) = runBlocking {
        withContext(Dispatchers.Default) {
            mergeSort(list, recursionLimit)
        }
    }
}
