package homework8.games

import homework8.players.Player

abstract class Game {
    enum class Mark {
        CROSS, NOUGHT
    }

    enum class PlayerPos {
        PLAYER_1, PLAYER_2;

        fun other() = when (this) {
            PLAYER_1 -> PLAYER_2
            PLAYER_2 -> PLAYER_1
        }
    }

    data class Cell(val x: Int, val y: Int)

    class Field(val size: Int) {
        val grid = MutableList(size) { MutableList<PlayerPos?>(size) { null } }

        fun getCell(cell: Cell) = grid[cell.y][cell.x]
        fun setCell(cell: Cell, playerPos: PlayerPos) {
            grid[cell.y][cell.x] = playerPos
        }

        fun clear() {
            grid.forEach { it.forEachIndexed { i, _ -> it[i] = null } }
        }

        fun isFilled() = grid.flatten().all { it != null }

        private fun checkWinnerInRow(i: Int): PlayerPos? {
            val set = grid[i].toSet()
            return if (set.size == 1) set.first()
            else null
        }

        private fun checkWinnerInColumn(i: Int): PlayerPos? {
            val set = grid.map { it[i] }.toSet()
            return if (set.size == 1) set.first()
            else null
        }

        private fun checkWinnerInDiagonals(): PlayerPos? {
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

    class Delegate(private val game: Game, private val playerPos: PlayerPos) {
        val field
            get() = game.field.grid.map { it.toList() }
        val size
            get() = game.field.size
        val turn
            get() = game.turn

        fun getMark(playerPos: PlayerPos) = with(game) { playerPos.toPlayerData().mark }
        fun getScore(playerPos: PlayerPos) = with(game) { playerPos.toPlayerData().score }

        fun ready() = game.onReady(playerPos)
        fun makeMove(cell: Cell) = game.onMakeMove(playerPos, cell)
        fun quit() = game.onQuit(playerPos)
    }

    private val field = Field(size)

    private var ended = false
    private var turn = PlayerPos.PLAYER_1

    abstract val player1: Player
    abstract val player2: Player
    private val player1Data = PlayerData(Mark.CROSS)
    private val player2Data = PlayerData(Mark.NOUGHT)

    private fun PlayerPos.toPlayer() = when (this) {
        PlayerPos.PLAYER_1 -> player1
        PlayerPos.PLAYER_2 -> player2
    }

    private fun PlayerPos.toPlayerData() = when (this) {
        PlayerPos.PLAYER_1 -> player1Data
        PlayerPos.PLAYER_2 -> player2Data
    }

    private fun start() {
        field.clear()
        player1.onStart()
        player2.onStart()
        turn.toPlayer().onMoveRequested()
    }

    private fun onReady(playerPos: PlayerPos) {
        playerPos.toPlayerData().ready = true
        if (player1Data.ready && player2Data.ready) {
            start()
            player1Data.ready = false
            player2Data.ready = false
        }
    }

    private fun onMakeMove(playerPos: PlayerPos, cell: Cell) {
        if (ended || playerPos != turn) return
        if (field.getCell(cell) != null) playerPos.toPlayer().onMoveRequested()
        else {
            field.setCell(cell, playerPos)
            playerPos.other().toPlayer().onOpponentMove(cell, playerPos)

            turn = playerPos.other()

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

    private fun onQuit(playerPos: PlayerPos) {
        ended = true
        playerPos.other().toPlayer().onOpponentLeft()
    }

    companion object {
        private const val size = 3
    }
}
