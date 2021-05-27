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

class RandomBotPlayer(override val delegate: Game.Delegate) : Player {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    override fun onStart() = Unit

    override fun onMoveRequested() {
        coroutineScope.launch {
            delay(THINKING_TIME.toLong())
            var cell: Cell
            do {
                cell = Cell(Random.nextInt(0, delegate.fieldSize), Random.nextInt(0, delegate.fieldSize))
            } while (delegate.field[cell.y][cell.x] != null)
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
