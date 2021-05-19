package homework8.games

import homework8.players.ControlledPlayer
import homework8.players.StrategicBotPlayer

class StrategicBotGame : OneControlledPlayerGame() {
    override val player1 = ControlledPlayer(Delegate(this, PlayerId.PLAYER_1))
    override val player2 = StrategicBotPlayer(Delegate(this, PlayerId.PLAYER_2))
    override val controlledPlayer1
        get() = this.player1
}
