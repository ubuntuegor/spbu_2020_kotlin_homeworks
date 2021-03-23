package common.io

/**
 * Prompt user for an integer.
 * @param prompt A message that is displayed before reading input.
 * @throws NumberFormatException If user provided a non-integer value.
 * @return The user-provided integer.
 */
fun promptInt(prompt: String): Int {
    print(prompt)
    return readLine()?.toIntOrNull() ?: throw NumberFormatException("Input has to be an integer")
}

/**
 * Prompt user for a list of integers.
 * @param prompt A message that is displayed before reading input.
 * @param terminateOn Stop reading input if it's equal to this string.
 * @return The user-provided list of integers.
 */
fun promptIntArray(prompt: String, terminateOn: String = ""): MutableList<Int> {
    println(prompt)
    val list = mutableListOf<Int>()
    var inputText = readLine()
    while (inputText != null && inputText != terminateOn) {
        val number = inputText.toIntOrNull()
        if (number == null) println("This is not an integer; ignoring")
        else list.add(number)
        inputText = readLine()
    }
    return list
}
