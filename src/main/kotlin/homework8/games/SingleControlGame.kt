package homework8.games

import homework8.players.ControlledPlayer

abstract class SingleControlGame : Game() {
    abstract val controlledPlayer1: ControlledPlayer
}
