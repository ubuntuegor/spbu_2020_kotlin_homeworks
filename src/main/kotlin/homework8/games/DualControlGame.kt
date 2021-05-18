package homework8.games

import homework8.players.ControlledPlayer

abstract class DualControlGame : SingleControlGame() {
    abstract val controlledPlayer2: ControlledPlayer
}
