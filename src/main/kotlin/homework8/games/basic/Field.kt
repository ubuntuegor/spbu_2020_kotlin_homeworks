package homework8.games.basic

class Field(val size: Int) {
    val grid = MutableList(size) { MutableList<PlayerId?>(size) { null } }

    fun getCell(cell: Cell) = grid[cell.y][cell.x]
    fun setCell(cell: Cell, playerId: PlayerId) {
        grid[cell.y][cell.x] = playerId
    }

    fun clear() {
        grid.forEach { it.forEachIndexed { i, _ -> it[i] = null } }
    }

    fun isFilled() = grid.flatten().all { it != null }

    private fun checkWinnerInRow(i: Int): PlayerId? {
        val set = grid[i].toSet()
        return if (set.size == 1) set.first()
        else null
    }

    private fun checkWinnerInColumn(i: Int): PlayerId? {
        val set = grid.map { it[i] }.toSet()
        return if (set.size == 1) set.first()
        else null
    }

    private fun checkWinnerInDiagonals(): PlayerId? {
        val set1 = grid.mapIndexed { i, l -> l[i] }.toSet()
        val set2 = grid.mapIndexed { i, l -> l[size - 1 - i] }.toSet()
        return if (set1.size == 1 && set1.first() != null) set1.first()
        else if (set2.size == 1) set2.first()
        else null
    }

    fun checkWinner() =
        (0 until size).mapNotNull { checkWinnerInRow(it) }.firstOrNull()
            ?: (0 until size).mapNotNull { checkWinnerInColumn(it) }.firstOrNull()
            ?: checkWinnerInDiagonals()
}
