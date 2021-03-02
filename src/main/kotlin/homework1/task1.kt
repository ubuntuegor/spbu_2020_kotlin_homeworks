package homework1

fun findFactorialIterative(n: Int): Int {
    var result = 1
    for (i in 1..n) result *= i
    return result
}

fun findFactorialRecursive(n: Int): Int {
    if (n <= 0) return 1
    return n * findFactorialRecursive(n - 1)
}

fun main() {
    print("Enter an integer: ")
    val n = readLine()?.toIntOrNull()

    if (n == null) error("Failed to read number from input")
    else if (n < 0) error("Can't calculate factorial of a negative")

    println("Factorial (iterative) of $n: ${findFactorialIterative(n)}")
    println("Factorial (recursive) of $n: ${findFactorialRecursive(n)}")
}
