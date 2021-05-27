package homework8.games

import homework8.games.basic.Cell
import homework8.games.basic.Game
import homework8.games.basic.Mark
import homework8.games.basic.PlayerData
import homework8.games.basic.PlayerId

abstract class LocalGame : Game() {
    override var turn = PlayerId.PLAYER_1

    override val player1Data = PlayerData(Mark.CROSS, "You")
    override val player2Data = PlayerData(Mark.NOUGHT, "Opponent")

    private val readyState = mutableMapOf<PlayerId, Boolean>()

    private fun PlayerId.toPlayer() = when (this) {
        PlayerId.PLAYER_1 -> player1
        PlayerId.PLAYER_2 -> player2
    }

    private fun start() {
        field.clear()
        player1.onStart()
        player2.onStart()
        turn.toPlayer().onMoveRequested()
    }

    override fun onReady(playerId: PlayerId) {
        readyState[playerId] = true
        if (readyState[PlayerId.PLAYER_1] == true && readyState[PlayerId.PLAYER_2] == true) {
            start()
            readyState.clear()
        }
    }

    override fun onMakeMove(playerId: PlayerId, cell: Cell) {
        if (playerId != turn) return
        if (field.getCell(cell) != null) playerId.toPlayer().onMoveRequested()
        else {
            field.setCell(cell, playerId)
            turn = playerId.other()
            turn.toPlayer().onOpponentMove(cell)
            val winner = field.checkWinner()
            when {
                winner != null -> {
                    player1.onGameResult(winner)
                    player2.onGameResult(winner)
                }
                field.isFilled() -> {
                    player1.onGameResult(null)
                    player2.onGameResult(null)
                }
                else -> turn.toPlayer().onMoveRequested()
            }
        }
    }
}
