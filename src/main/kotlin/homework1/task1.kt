package homework1

fun findFactorialIterative(n: Int): Int {
    var result = 1
    for (i in 1..n) result *= i
    return result
}

fun findFactorialRecursive(n: Int): Int {
    if (n <= 1) return 1
    return n * findFactorialRecursive(n - 1)
}

fun main() {
    print("Enter an integer: ")
    val n = readLine()?.toIntOrNull()

    if (n == null || n < 1) {
        println("Failed to read input")
        return
    }

    println("Factorial (iterative) of $n: ${findFactorialIterative(n)}")
    println("Factorial (recursive) of $n: ${findFactorialRecursive(n)}")
}
