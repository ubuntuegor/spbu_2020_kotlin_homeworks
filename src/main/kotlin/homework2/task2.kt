package homework2

import common.io.promptIntArray

fun <T> List<T>.removeDuplicates(): List<T> {
    val lastIndices = this.zip(this.indices).toMap()
    return this.filterIndexed { index, value -> index == lastIndices[value] }
}

fun main() {
    val result = promptIntArray("Enter some integers, each on new line. Leave a line empty to stop:")
        .removeDuplicates()
    println("I have left only the last occurrence of each number:")
    println(result)
}
