package homework8.players

import homework8.games.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BotRandom(override val delegate: Game.Delegate) : Player(delegate) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override var onMoveRequested: () -> Unit = {
        coroutineScope.launch {
            delay(THINKING_TIME.toLong())
            var cell: Game.Cell
            do {
                cell = Game.Cell(Random.nextInt(0, delegate.size), Random.nextInt(0, delegate.size))
            } while (delegate.field[cell.y][cell.x] != null)
            delegate.makeMove(cell)
        }
    }

    override var onGameResult: (Game.PlayerPos?) -> Unit = {
        delegate.ready()
    }

    override var onOpponentLeft: () -> Unit = {
        coroutineScope.cancel()
    }

    init {
        delegate.ready()
    }

    companion object {
        private const val THINKING_TIME = 1000
    }
}
