package common.error

import kotlin.system.exitProcess

fun die(message: String): Nothing {
    println("ERROR: $message")
    exitProcess(1)
}
