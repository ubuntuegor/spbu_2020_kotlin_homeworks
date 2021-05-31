package homework8.players

import homework8.games.basic.Cell
import homework8.games.basic.Game
import homework8.games.basic.PlayerId

interface Player {
    val delegate: Game.Delegate
    fun onStart()
    fun onMoveRequested()
    fun onOpponentMove(cell: Cell)
    fun onGameResult(winner: PlayerId?)
    fun onOpponentLeft()
}
