package homework7

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

operator fun List<Int>.times(multiplier: List<Int>): Int {
    if (this.size != multiplier.size) throw IllegalArgumentException("Cannot multiply lists of different size")
    return this.foldIndexed(0) { index, acc, elem -> acc + elem * multiplier[index] }
}

data class Matrix(val matrix: List<List<Int>>) {
    private val width = matrix.firstOrNull()?.size ?: 0
    private val height = matrix.size

    private val columns = (1..width).map { index -> matrix.map { it[index - 1] } }
    private val rows = matrix

    suspend operator fun times(multiplier: Matrix) = coroutineScope {
        if (width != multiplier.height) {
            throw IllegalArgumentException("Cannot multiply matrix with width $width by matrix with height $height")
        }

        if (height == 0 || width == 0) Matrix(listOf())
        else Matrix(
            rows.flatMap { row ->
                multiplier.columns.map { column ->
                    async(Dispatchers.Default) { row * column }
                }
            }.awaitAll().chunked(multiplier.width)
        )
    }
}
