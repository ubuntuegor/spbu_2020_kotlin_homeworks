package common

import common.exceptions.InvalidInputException

fun promptInt(prompt: String): Int {
    print(prompt)
    return readLine()?.toIntOrNull() ?: throw InvalidInputException()
}
