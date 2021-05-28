package homework7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

operator fun List<Int>.times(multiplier: List<Int>): Int {
    require(this.size == multiplier.size) { "Cannot multiply lists of different size" }
    return this.foldIndexed(0) { index, acc, elem -> acc + elem * multiplier[index] }
}

data class Matrix(val matrix: List<List<Int>>) {
    private val width = matrix.firstOrNull()?.size ?: 0
    private val height = if (width != 0) matrix.size else 0

    private val columns: List<List<Int>>
    private val rows = matrix

    init {
        matrix.forEach {
            require(it.size == width) { "Cannot create a matrix from inconsistently sized lists" }
        }

        columns = (0 until width).map { index -> matrix.map { it[index] } }
    }

    suspend operator fun times(multiplier: Matrix) = coroutineScope {
        require(width == multiplier.height) { "Cannot multiply matrix with width $width by matrix with height $height" }

        if (height == 0 || multiplier.width == 0) Matrix(listOf())
        else Matrix(
            rows.flatMap { row ->
                multiplier.columns.map { column ->
                    async(Dispatchers.Default) { row * column }
                }
            }.awaitAll().chunked(multiplier.width)
        )
    }
}
