package homework8.controllers

import homework8.games.DualControlGame
import homework8.games.Game
import homework8.games.RandomBotGame
import homework8.games.StrategicBotGame
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

    private val player = game.controlledPlayer1
    val delegate = game.controlledPlayer1.delegate

    val localField =
        List(delegate.size) { List(delegate.size) { SimpleObjectProperty<Game.Mark>() } }

    val messageProperty = SimpleStringProperty("Waiting for opponent")
    private var message: String by messageProperty
    val disableFieldProperty = SimpleBooleanProperty(false)
    val enableRestartProperty = SimpleBooleanProperty(false)
    val turnProperty = SimpleObjectProperty(delegate.turn)
    val player1NameProperty = SimpleStringProperty(
        when (game) {
            is DualControlGame -> "Player 1"
            else -> "You"
        }
    )
    val player2NameProperty = SimpleStringProperty(
        when (game) {
            is DualControlGame -> "Player 2"
            is RandomBotGame, is StrategicBotGame -> "Bot"
            else -> "Opponent"
        }
    )
    val player1ScoreProperty = SimpleIntegerProperty(0)
    val player2ScoreProperty = SimpleIntegerProperty(0)

    init {
        player.init(onStart = {
            runLater {
                clearLocalField()
                messageProperty.set("")
                if (game is DualControlGame || delegate.turn == delegate.playerId) disableFieldProperty.set(false)
                enableRestartProperty.set(false)
                turnProperty.set(delegate.turn)
            }
        }, onMoveRequested = {
            runLater {
                if (game !is DualControlGame) {
                    disableFieldProperty.set(false)
                }
            }
        }, onMove = { cell, playerPos ->
            runLater {
                localField[cell.y][cell.x].set(playerPos.toMark())
                turnProperty.set(delegate.turn)
            }
        }, onGameResult = { winner ->
            runLater {
                message = when (winner?.toMark()) {
                    null -> "It's a tie!"
                    Game.Mark.CROSS -> "Crosses won!"
                    Game.Mark.NOUGHT -> "Noughts won!"
                }
                player1ScoreProperty.set(delegate.getScore(delegate.playerId))
                player2ScoreProperty.set(delegate.getScore(delegate.playerId.other()))
                disableFieldProperty.set(true)
                enableRestartProperty.set(true)
            }
        }, onOpponentLeft = {
            runLater {
                message = "Opponent left the game."
                disableFieldProperty.set(true)
                enableRestartProperty.set(false)
            }
        })

        if (game is DualControlGame) game.controlledPlayer2.init()

        ready()
    }

    private fun Game.PlayerId.toMark() = delegate.getMark(this)

    private fun clearLocalField() {
        localField.forEach { it.forEachIndexed { i, _ -> it[i].set(null) } }
    }

    fun ready() {
        delegate.ready()
        if (game is DualControlGame) {
            game.controlledPlayer2.delegate.ready()
        }
    }

    fun makeMove(cell: Game.Cell) {
        if (delegate.field[cell.y][cell.x] != null) return
        if (game is DualControlGame) {
            when (delegate.turn) {
                Game.PlayerId.PLAYER_1 -> game.controlledPlayer1
                Game.PlayerId.PLAYER_2 -> game.controlledPlayer2
            }.delegate.makeMove(cell)
        } else {
            disableFieldProperty.set(true)
            delegate.makeMove(cell)
        }
    }

    fun quit() {
        delegate.quit()
    }
}
