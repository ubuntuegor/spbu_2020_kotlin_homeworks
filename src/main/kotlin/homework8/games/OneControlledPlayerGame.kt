package homework8.games

import homework8.players.ControlledPlayer

abstract class OneControlledPlayerGame : Game() {
    abstract val controlledPlayer1: ControlledPlayer
}
