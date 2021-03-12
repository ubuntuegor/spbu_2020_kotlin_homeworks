package common.error

import kotlin.system.exitProcess

/**
 * Terminate the running program with an error code and a message.
 * @param message Error message to display.
 */
fun exitWithError(message: String): Nothing {
    println("ERROR: $message")
    exitProcess(1)
}
