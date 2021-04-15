package homework5

import kotlin.math.pow

interface HashFunctionWrapper<in T> {
    fun hashOf(value: T, modulus: Int): Int
}

object HashCode : HashFunctionWrapper<Any> {
    override fun hashOf(value: Any, modulus: Int) =
        value.hashCode() % modulus
}

object RollingHash : HashFunctionWrapper<String> {
    private const val prime: Int = 2

    override fun hashOf(value: String, modulus: Int): Int {
        var hash = 0
        value.forEachIndexed { i, c ->
            hash += c.toInt() * prime.toDouble().pow(value.length - 1 - i).toInt() % modulus
            hash %= modulus
        }
        return hash
    }
}
