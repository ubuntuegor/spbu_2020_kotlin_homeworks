package homework1

import common.error.die

fun String.countOccurrences(needle: String): Int {
    if (needle == "") return this.length + 1

    var occurrences = 0
    var index = this.indexOf(needle)
    while (index != -1) {
        occurrences++
        index = this.indexOf(needle, index + 1)
    }
    return occurrences
}

fun main() {
    print("Enter string to search in: ")
    val haystack = readLine() ?: die("Failed to read input")
    print("Enter string to find: ")
    val needle = readLine() ?: die("Failed to read input")

    println("Found ${haystack.countOccurrences(needle)} occurrences")
}
