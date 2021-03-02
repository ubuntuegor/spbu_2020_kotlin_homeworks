package common.io

fun promptInt(prompt: String): Int {
    print(prompt)
    return readLine()?.toIntOrNull() ?: throw NumberFormatException("Input has to be an integer")
}
