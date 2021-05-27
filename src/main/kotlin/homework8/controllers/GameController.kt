package homework8.controllers

import homework8.games.basic.Cell
import homework8.games.basic.Mark
import homework8.games.basic.PlayerId
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.Controller
import tornadofx.booleanBinding
import tornadofx.runLater

class GameController : Controller() {
    override val scope = super.scope as GameViewScope

    enum class State(val message: String? = null) {
        READY("Waiting for opponent..."),
        PLAYER_1_MOVE,
        PLAYER_2_MOVE,
        NOUGHTS_WON("Noughts won!"),
        CROSSES_WON("Crosses won!"),
        TIE("It's a tie!"),
        ENDED("Opponent has left.")
    }

    val delegate = scope.player1.delegate

    val localField = List(delegate.fieldSize) { List(delegate.fieldSize) { SimpleObjectProperty<Mark>() } }

    val stateProperty = SimpleObjectProperty(State.READY)
    val enableFieldProperty = stateProperty.booleanBinding {
        it == State.PLAYER_1_MOVE || (scope.player2 != null && it == State.PLAYER_2_MOVE)
    }

    val player1ScoreProperty = SimpleIntegerProperty(0)
    val player2ScoreProperty = SimpleIntegerProperty(0)

    init {
        scope.player1.init(onStart = {
            runLater {
                clearLocalField()
                stateProperty.set(
                    when (delegate.turn) {
                        PlayerId.PLAYER_1 -> State.PLAYER_1_MOVE
                        PlayerId.PLAYER_2 -> State.PLAYER_2_MOVE
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
                if (winner != null) winner.toScore().value++
                when (winner?.toMark()) {
                    null -> stateProperty.set(State.TIE)
                    Mark.CROSS -> {
                        stateProperty.set(State.CROSSES_WON)
                    }
                    Mark.NOUGHT -> stateProperty.set(State.NOUGHTS_WON)
                }
            }
        }, onOpponentLeft = {
            runLater {
                stateProperty.set(State.ENDED)
            }
        })

        scope.player2?.init()

        ready()
    }

    private fun PlayerId.toMark() = delegate.getPlayerData(this).mark
    private fun PlayerId.toScore() = when (this) {
        PlayerId.PLAYER_1 -> player1ScoreProperty
        PlayerId.PLAYER_2 -> player2ScoreProperty
    }

    private fun clearLocalField() {
        localField.forEach { it.forEachIndexed { i, _ -> it[i].set(null) } }
    }

    fun ready() {
        stateProperty.set(State.READY)
        delegate.ready()
        scope.player2?.delegate?.ready()
    }

    fun makeMove(cell: Cell) {
        if (delegate.field[cell.y][cell.x] != null) return
        if (scope.player2 != null) {
            localField[cell.y][cell.x].set(delegate.turn.toMark())
            stateProperty.set(
                when (delegate.turn) {
                    PlayerId.PLAYER_1 -> State.PLAYER_2_MOVE
                    PlayerId.PLAYER_2 -> State.PLAYER_1_MOVE
                }
            )
            when (delegate.turn) {
                PlayerId.PLAYER_1 -> scope.player1
                PlayerId.PLAYER_2 -> scope.player2
            }.delegate.makeMove(cell)
        } else {
            localField[cell.y][cell.x].set(delegate.playerId.toMark())
            stateProperty.set(State.PLAYER_2_MOVE)
            delegate.makeMove(cell)
        }
    }
}
