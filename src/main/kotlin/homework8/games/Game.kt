package homework8.games

import homework8.players.Player

abstract class Game {
    enum class Mark {
        CROSS, NOUGHT
    }

    enum class PlayerId {
        PLAYER_1, PLAYER_2;

        fun other() = when (this) {
            PLAYER_1 -> PLAYER_2
            PLAYER_2 -> PLAYER_1
        }
    }

    data class Cell(val x: Int, val y: Int)

    class Field(val size: Int) {
        val grid = MutableList(size) { MutableList<PlayerId?>(size) { null } }

        fun getCell(cell: Cell) = grid[cell.y][cell.x]
        fun setCell(cell: Cell, playerId: PlayerId) {
            grid[cell.y][cell.x] = playerId
        }

        fun clear() {
            grid.forEach { it.forEachIndexed { i, _ -> it[i] = null } }
        }

        fun isFilled() = grid.flatten().all { it != null }

        private fun checkWinnerInRow(i: Int): PlayerId? {
            val set = grid[i].toSet()
            return if (set.size == 1) set.first()
            else null
        }

        private fun checkWinnerInColumn(i: Int): PlayerId? {
            val set = grid.map { it[i] }.toSet()
            return if (set.size == 1) set.first()
            else null
        }

        private fun checkWinnerInDiagonals(): PlayerId? {
            val set1 = grid.mapIndexed { i, l -> l[i] }.toSet()
            val set2 = grid.mapIndexed { i, l -> l[size - 1 - i] }.toSet()
            return if (set1.size == 1 && set1.first() != null) set1.first()
            else if (set2.size == 1) set2.first()
            else null
        }

        fun checkWinner() =
            (0 until size).mapNotNull { checkWinnerInRow(it) }.firstOrNull()
                ?: (0 until size).mapNotNull { checkWinnerInColumn(it) }.firstOrNull()
                ?: checkWinnerInDiagonals()
    }

    class PlayerData(val mark: Mark) {
        var ready = false
        var score = 0
    }

    class Delegate(private val game: Game, val playerId: PlayerId) {
        val field
            get() = game.field.grid.map { it.toList() }
        val size
            get() = game.field.size
        val turn
            get() = game.turn

        fun getMark(playerId: PlayerId) = with(game) { playerId.toPlayerData().mark }
        fun getScore(playerId: PlayerId) = with(game) { playerId.toPlayerData().score }

        fun ready() = game.onReady(playerId)
        fun makeMove(cell: Cell) = game.onMakeMove(playerId, cell)
        fun quit() = game.onQuit(playerId)
    }

    private val field = Field(SIZE)

    private var ended = false
    private var turn = PlayerId.PLAYER_1

    protected abstract val player1: Player
    protected abstract val player2: Player
    private val player1Data = PlayerData(Mark.CROSS)
    private val player2Data = PlayerData(Mark.NOUGHT)

    private fun PlayerId.toPlayer() = when (this) {
        PlayerId.PLAYER_1 -> player1
        PlayerId.PLAYER_2 -> player2
    }

    private fun PlayerId.toPlayerData() = when (this) {
        PlayerId.PLAYER_1 -> player1Data
        PlayerId.PLAYER_2 -> player2Data
    }

    private fun start() {
        field.clear()
        player1.onStart()
        player2.onStart()
        turn.toPlayer().onMoveRequested()
    }

    private fun onReady(playerId: PlayerId) {
        playerId.toPlayerData().ready = true
        if (player1Data.ready && player2Data.ready) {
            start()
            player1Data.ready = false
            player2Data.ready = false
        }
    }

    private fun onMakeMove(playerId: PlayerId, cell: Cell) {
        if (ended || playerId != turn) return
        if (field.getCell(cell) != null) playerId.toPlayer().onMoveRequested()
        else {
            field.setCell(cell, playerId)

            turn = playerId.other()

            turn.toPlayer().onOpponentMove(cell)

            val winner = field.checkWinner()
            when {
                winner != null -> {
                    winner.toPlayerData().score++
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

    private fun onQuit(playerId: PlayerId) {
        ended = true
        playerId.other().toPlayer().onOpponentLeft()
    }

    companion object {
        private const val SIZE = 3
    }
}
