package homework8.games

import homework8.players.ControlledPlayer

abstract class TwoControlledPlayersGame : OneControlledPlayerGame() {
    abstract val controlledPlayer2: ControlledPlayer
}
