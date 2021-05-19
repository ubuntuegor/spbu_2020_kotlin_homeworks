package homework8.controllers

import homework8.games.TwoControlledPlayersGame
import homework8.games.Game
import homework8.games.RandomBotGame
import homework8.games.StrategicBotGame
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Controller
import tornadofx.booleanBinding
import tornadofx.runLater

class GameController : Controller() {
    enum class State {
        READY, PLAYER_1_MOVE, PLAYER_2_MOVE, NOUGHTS_WON, CROSSES_WON, TIE, ENDED
    }

    private val game = (scope as GameScope).game
    private val player = game.controlledPlayer1
    val delegate = game.controlledPlayer1.delegate

    val localField =
        List(delegate.size) { List(delegate.size) { SimpleObjectProperty<Game.Mark>() } }

    val stateProperty = SimpleObjectProperty(State.READY)
    val enableFieldProperty = stateProperty.booleanBinding {
        it == State.PLAYER_1_MOVE || (game is TwoControlledPlayersGame && it == State.PLAYER_2_MOVE)
    }

    val player1NameProperty = SimpleStringProperty(
        when (game) {
            is TwoControlledPlayersGame -> "Player 1"
            else -> "You"
        }
    )
    val player2NameProperty = SimpleStringProperty(
        when (game) {
            is TwoControlledPlayersGame -> "Player 2"
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
                stateProperty.set(
                    when (delegate.turn) {
                        Game.PlayerId.PLAYER_1 -> State.PLAYER_1_MOVE
                        Game.PlayerId.PLAYER_2 -> State.PLAYER_2_MOVE
                    }
                )
            }
        }, onMoveRequested = {
            runLater {
                stateProperty.set(State.PLAYER_1_MOVE)
            }
        }, onOpponentMove = { cell ->
            runLater {
                localField[cell.y][cell.x].set(delegate.playerId.other().toMark())
            }
        }, onGameResult = { winner ->
            runLater {
                when (winner?.toMark()) {
                    null -> stateProperty.set(State.TIE)
                    Game.Mark.CROSS -> stateProperty.set(State.CROSSES_WON)
                    Game.Mark.NOUGHT -> stateProperty.set(State.NOUGHTS_WON)
                }
                player1ScoreProperty.set(delegate.getScore(delegate.playerId))
                player2ScoreProperty.set(delegate.getScore(delegate.playerId.other()))
            }
        }, onOpponentLeft = {
            runLater {
                stateProperty.set(State.ENDED)
            }
        })

        if (game is TwoControlledPlayersGame) game.controlledPlayer2.init()

        ready()
    }

    private fun Game.PlayerId.toMark() = delegate.getMark(this)

    private fun clearLocalField() {
        localField.forEach { it.forEachIndexed { i, _ -> it[i].set(null) } }
    }

    fun ready() {
        stateProperty.set(State.READY)
        delegate.ready()
        if (game is TwoControlledPlayersGame) game.controlledPlayer2.delegate.ready()
    }

    fun makeMove(cell: Game.Cell) {
        if (delegate.field[cell.y][cell.x] != null) return
        if (game is TwoControlledPlayersGame) {
            localField[cell.y][cell.x].set(delegate.turn.toMark())
            stateProperty.set(
                when (delegate.turn) {
                    Game.PlayerId.PLAYER_1 -> State.PLAYER_2_MOVE
                    Game.PlayerId.PLAYER_2 -> State.PLAYER_1_MOVE
                }
            )
            when (delegate.turn) {
                Game.PlayerId.PLAYER_1 -> game.controlledPlayer1
                Game.PlayerId.PLAYER_2 -> game.controlledPlayer2
            }.delegate.makeMove(cell)
        } else {
            localField[cell.y][cell.x].set(delegate.playerId.toMark())
            stateProperty.set(State.PLAYER_2_MOVE)
            delegate.makeMove(cell)
        }
    }

    fun quit() {
        stateProperty.set(State.ENDED)
        delegate.quit()
    }
}
