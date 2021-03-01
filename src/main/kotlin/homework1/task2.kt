package homework1

fun countOccurrences(needle: String, haystack: String): Int {
    var occurrences = 0
    var index = haystack.indexOf(needle)
    while (index != -1) {
        index = haystack.indexOf(needle, index + 1)
        occurrences++
    }
    return occurrences
}

fun main() {
    print("Enter string to find: ")
    val needle = readLine()
    print("Enter string to search in: ")
    val haystack = readLine()

    if (needle == null || haystack == null) {
        println("Failed to read input")
        return
    }

    val result = countOccurrences(needle, haystack)
    println("Found $result occurrences")
}
