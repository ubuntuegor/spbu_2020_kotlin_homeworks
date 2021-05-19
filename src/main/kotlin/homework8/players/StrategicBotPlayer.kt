package homework8.players

import homework8.games.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class StrategicBotPlayer(override val delegate: Game.Delegate) : Player {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onStart() = Unit

    private fun getRows(numberedField: List<List<Pair<Game.Cell, Game.PlayerId?>>>) =
        numberedField

    private fun getColumns(numberedField: List<List<Pair<Game.Cell, Game.PlayerId?>>>) =
        (0 until delegate.size).map { index -> numberedField.map { it[index] } }

    private fun getDiagonals(numberedField: List<List<Pair<Game.Cell, Game.PlayerId?>>>) =
        listOf(
            numberedField.mapIndexed { i, l -> l[i] },
            numberedField.mapIndexed { i, l -> l[delegate.size - 1 - i] }
        )

    private fun findRemaining(playerId: Game.PlayerId): Game.Cell? {
        val numberedField = delegate.field.mapIndexed { y, row -> row.mapIndexed { x, p -> Pair(Game.Cell(x, y), p) } }
        val variants = getRows(numberedField) + getColumns(numberedField) + getDiagonals(numberedField)
        return variants.firstOrNull { column ->
            column.count { it.second == playerId } == 2 && column.count { it.second == null } == 1
        }?.firstOrNull { it.second == null }?.first
    }

    private fun getRandomCell(): Game.Cell {
        var cell: Game.Cell
        do {
            cell = Game.Cell(Random.nextInt(0, delegate.size), Random.nextInt(0, delegate.size))
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

    override fun onOpponentMove(cell: Game.Cell) = Unit

    override fun onGameResult(winner: Game.PlayerId?) {
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
