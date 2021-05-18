package homework8.players

import homework8.games.Game
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
            var cell: Game.Cell
            do {
                cell = Game.Cell(Random.nextInt(0, delegate.size), Random.nextInt(0, delegate.size))
            } while (delegate.field[cell.y][cell.x] != null)
            delegate.makeMove(cell)
        }
    }

    override fun onMove(cell: Game.Cell, playerId: Game.PlayerId) = Unit

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
