package common.error

import kotlin.system.exitProcess

fun exitWithError(message: String): Nothing {
    println("ERROR: $message")
    exitProcess(1)
}
