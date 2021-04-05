package homework5

import java.io.File

fun main(args: Array<String>) {
    val input = File(args[0]).readText().trim()
    println("Input: $input")

    val tree = ExpressionTree.parse(input)
    println("Output: $tree")
    println("Result: ${tree.calculateValue()}")
}
