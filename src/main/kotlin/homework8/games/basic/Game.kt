package homework8.games.basic

import homework8.players.Player

abstract class Game {
    inner class Delegate(val playerId: PlayerId) {
        val field
            get() = this@Game.field.grid.map { it.toList() }
        val fieldSize
            get() = this@Game.field.size
        val turn
            get() = this@Game.turn

        fun getPlayerData(playerId: PlayerId) = playerId.toPlayerData()

        fun ready() = onReady(playerId)
        fun makeMove(cell: Cell) = onMakeMove(playerId, cell)
    }

    protected val field = Field(SIZE)
    protected abstract var turn: PlayerId

    protected abstract val player1: Player
    protected abstract val player2: Player
    protected abstract val player1Data: PlayerData
    protected abstract val player2Data: PlayerData

    protected fun PlayerId.toPlayerData() = when (this) {
        PlayerId.PLAYER_1 -> player1Data
        PlayerId.PLAYER_2 -> player2Data
    }

    protected abstract fun onReady(playerId: PlayerId)

    protected abstract fun onMakeMove(playerId: PlayerId, cell: Cell)

    companion object {
        private const val SIZE = 3
    }
}
