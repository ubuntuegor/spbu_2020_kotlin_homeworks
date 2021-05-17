package homework8.controllers

import homework8.games.Game
import homework8.games.LocalGame
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.runLater
import tornadofx.setValue

class GameController : Controller() {
    private val game = (scope as GameScope).game

    val delegate = game.player1.delegate

    val localField =
        List(delegate.size) { List(delegate.size) { SimpleObjectProperty<Game.Mark>() } }

    val messageProperty = SimpleStringProperty("Waiting for opponent")
    private var message: String by messageProperty
    val disableFieldProperty = SimpleBooleanProperty(false)
    val enableRestartProperty = SimpleBooleanProperty(false)
    val player1ScoreProperty = SimpleIntegerProperty()
    val player2ScoreProperty = SimpleIntegerProperty()
    val turnProperty = SimpleObjectProperty(delegate.turn)

    init {
        game.player1.onStart = {
            runLater {
                clearLocalField()
                messageProperty.set("")
                if (game is LocalGame || delegate.turn == Game.PlayerPos.PLAYER_1) disableFieldProperty.set(false)
                enableRestartProperty.set(false)
                turnProperty.set(delegate.turn)
            }
        }
        if (game !is LocalGame) {
            game.player1.onMoveRequested = {
                runLater {
                    disableFieldProperty.set(false)
                    turnProperty.set(Game.PlayerPos.PLAYER_1)
                }
            }
            game.player1.onOpponentMove = { cell, playerPos ->
                runLater {
                    localField[cell.y][cell.x].set(playerPos.toMark())
                }
            }
        }
        game.player1.onGameResult = { winner ->
            runLater {
                message = when (winner?.toMark()) {
                    null -> "It's a tie!"
                    Game.Mark.CROSS -> "Crosses won!"
                    Game.Mark.NOUGHT -> "Noughts won!"
                }
                player1ScoreProperty.set(delegate.getScore(Game.PlayerPos.PLAYER_1))
                player2ScoreProperty.set(delegate.getScore(Game.PlayerPos.PLAYER_2))
                disableFieldProperty.set(true)
                enableRestartProperty.set(true)
            }
        }
        game.player1.onOpponentLeft = {
            runLater {
                message = "Opponent left the game"
                disableFieldProperty.set(true)
                enableRestartProperty.set(false)
            }
        }
        ready()
    }

    private fun Game.PlayerPos.toDelegate() = when (this) {
        Game.PlayerPos.PLAYER_1 -> game.player1.delegate
        Game.PlayerPos.PLAYER_2 -> game.player2.delegate
    }

    private fun Game.PlayerPos.toMark() = delegate.getMark(this)

    private fun clearLocalField() {
        localField.forEach { it.forEachIndexed { i, _ -> it[i].set(null) } }
    }

    fun ready() {
        delegate.ready()
        if (game is LocalGame) {
            game.player2.delegate.ready()
        }
    }

    fun makeMove(cell: Game.Cell) {
        if (delegate.field[cell.y][cell.x] != null) return
        localField[cell.y][cell.x].set(delegate.turn.toMark())
        turnProperty.set(delegate.turn.other())
        if (game is LocalGame) {
            delegate.turn.toDelegate().makeMove(cell)
        } else {
            disableFieldProperty.set(true)
            delegate.makeMove(cell)
        }
    }

    fun quit() {
        delegate.quit()
    }
}
