package test1

class Vector<T : ArithmeticAvailable<T>>(val list: List<T>) {
    val size: Int
        get() = list.size

    private fun checkCompatibility(other: Vector<T>) =
        require(other.size == size) { "Cannot operate on vectors of size $size and ${other.size}" }

    operator fun plus(other: Vector<T>): Vector<T> {
        checkCompatibility(other)
        return Vector(list.zip(other.list) { a, b -> a + b })
    }

    operator fun minus(other: Vector<T>): Vector<T> {
        checkCompatibility(other)
        return Vector(list.zip(other.list) { a, b -> a - b })
    }

    operator fun times(other: Vector<T>): T {
        checkCompatibility(other)
        require(size > 0) { "Cannot multiply empty vectors" }
        return list.zip(other.list) { a, b -> a * b }.reduce { acc, t -> acc + t }
    }

    fun isNull() = list.all { it.isZero() }
}

interface ArithmeticAvailable<T : ArithmeticAvailable<T>> {
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(other: T): T
    fun isZero(): Boolean
}

class ArithmeticInt(private val actual: Int) : ArithmeticAvailable<ArithmeticInt> {
    override fun plus(other: ArithmeticInt) = ArithmeticInt(actual + other.actual)
    override fun minus(other: ArithmeticInt) = ArithmeticInt(actual - other.actual)
    override fun times(other: ArithmeticInt) = ArithmeticInt(actual * other.actual)
    override fun isZero() = this.equals(0)
}
