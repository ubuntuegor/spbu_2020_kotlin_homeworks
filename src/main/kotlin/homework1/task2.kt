package homework1

import common.error.exitWithError

fun String.countOccurrences(needle: String): Int {
    if (needle == "") throw IllegalArgumentException("Can't count occurrences of an empty string")

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
    val haystack = readLine() ?: exitWithError("Failed to read input")
    print("Enter string to find: ")
    val needle = readLine() ?: exitWithError("Failed to read input")

    try {
        println("Found ${haystack.countOccurrences(needle)} occurrences")
    } catch (e: IllegalArgumentException) {
        println("ERROR: ${e.message}")
    }
}
