package homework8.players

import homework8.games.Game

interface Player {
    val delegate: Game.Delegate
    fun onStart()
    fun onMoveRequested()
    fun onMove(cell: Game.Cell, playerId: Game.PlayerId)
    fun onGameResult(winner: Game.PlayerId?)
    fun onOpponentLeft()
}
