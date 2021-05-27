package homework8.players

import homework8.games.basic.Cell
import homework8.games.basic.Game
import homework8.games.basic.PlayerId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class StrategicBotPlayer(override val delegate: Game.Delegate) : Player {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onStart() = Unit

    private fun getRows(numberedField: List<List<Pair<Cell, PlayerId?>>>) =
        numberedField

    private fun getColumns(numberedField: List<List<Pair<Cell, PlayerId?>>>) =
        (0 until delegate.fieldSize).map { index -> numberedField.map { it[index] } }

    private fun getDiagonals(numberedField: List<List<Pair<Cell, PlayerId?>>>) =
        listOf(
            numberedField.mapIndexed { i, l -> l[i] },
            numberedField.mapIndexed { i, l -> l[delegate.fieldSize - 1 - i] }
        )

    private fun findRemaining(playerId: PlayerId): Cell? {
        val numberedField = delegate.field.mapIndexed { y, row -> row.mapIndexed { x, p -> Pair(Cell(x, y), p) } }
        val variants = getRows(numberedField) + getColumns(numberedField) + getDiagonals(numberedField)
        return variants.firstOrNull { column ->
            column.count { it.second == playerId } == 2 && column.count { it.second == null } == 1
        }?.firstOrNull { it.second == null }?.first
    }

    private fun getRandomCell(): Cell {
        var cell: Cell
        do {
            cell = Cell(Random.nextInt(0, delegate.fieldSize), Random.nextInt(0, delegate.fieldSize))
        } while (delegate.field[cell.y][cell.x] != null)
        return cell
    }

    override fun onMoveRequested() {
        coroutineScope.launch {
            delay(THINKING_TIME.toLong())
            val cell = findRemaining(delegate.playerId) ?: findRemaining(delegate.playerId.other()) ?: getRandomCell()
            delegate.makeMove(cell)
        }
    }

    override fun onOpponentMove(cell: Cell) = Unit

    override fun onGameResult(winner: PlayerId?) {
        delegate.ready()
    }

    override fun onOpponentLeft() {
        coroutineScope.cancel()
    }

    init {
        delegate.ready()
    }

    companion object {
        private const val THINKING_TIME = 1000
    }
}
