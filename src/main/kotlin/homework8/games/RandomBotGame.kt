package homework8.games

import homework8.players.RandomBotPlayer
import homework8.players.ControlledPlayer

class RandomBotGame : OneControlledPlayerGame() {
    override val player1 = ControlledPlayer(Delegate(this, PlayerId.PLAYER_1))
    override val player2 = RandomBotPlayer(Delegate(this, PlayerId.PLAYER_2))
    override val controlledPlayer1
        get() = this.player1
}
