package homework5

import common.error.exitWithError
import java.io.File
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    if (args.isEmpty()) exitWithError("No file path provided in program arguments.")
    val input = try {
        File(args[0]).readText()
    } catch (e: FileNotFoundException) {
        exitWithError(e.message ?: "File not found.")
    }
    println("Input: $input")

    val tree = try {
        ExpressionTree.parse(input)
    } catch (e: IllegalArgumentException) {
        exitWithError("Couldn't parse expression: ${e.message}.")
    }
    println("Output: $tree")
    println("Result: ${tree.calculateValue()}")
}
