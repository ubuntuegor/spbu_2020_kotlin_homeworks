package common.io

import common.error.exitWithError

/**
 * Prompt user for a string.
 * @param prompt A message that is displayed before reading input.
 * @throws IllegalArgumentException If input is unavailable.
 * @return The user-provided string.
 */
fun promptString(prompt: String): String {
    print(prompt)
    return readLine() ?: throw IllegalArgumentException("Can't read input")
}

/**
 * Ask user for text input in the following format - "Enter <name>: ", or die.
 * @param name What is requested from the user.
 * @return The user-provided string.
 */
fun askFor(name: String): String {
    try {
        return promptString("Enter $name: ").trim()
    } catch (e: IllegalArgumentException) {
        exitWithError(e.message ?: "Can't read input")
    }
}

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
