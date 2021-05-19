package homework8.players

import homework8.games.Game

interface Player {
    val delegate: Game.Delegate
    fun onStart()
    fun onMoveRequested()
    fun onOpponentMove(cell: Game.Cell)
    fun onGameResult(winner: Game.PlayerId?)
    fun onOpponentLeft()
}
