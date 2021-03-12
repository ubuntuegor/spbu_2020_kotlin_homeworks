package homework2

const val TEST_DATA = "1, 2, 3, 4, 1, 4, 5, 3"

fun <T> List<T>.removeDuplicates(): List<T> {
    val lastIndices = this.zip(this.indices).toMap()
    return this.filterIndexed { index, value -> index == lastIndices[value] }
}

fun main() {
    val result = TEST_DATA.split(", ")
        .removeDuplicates()
    println(result)
}
