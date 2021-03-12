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
