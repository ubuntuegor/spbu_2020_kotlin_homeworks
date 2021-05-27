package homework8.games

import homework8.games.basic.Mark
import homework8.games.basic.PlayerData
import homework8.games.basic.PlayerId
import homework8.players.RandomBotPlayer
import homework8.players.ControlledPlayer

class RandomBotGame : LocalGame() {
    override val player1 = ControlledPlayer(Delegate(PlayerId.PLAYER_1))
    override val player2 = RandomBotPlayer(Delegate(PlayerId.PLAYER_2))
    override val player1Data = PlayerData(Mark.CROSS, "You")
    override val player2Data = PlayerData(Mark.NOUGHT, "Bot")
    val controlledPlayer1
        get() = this.player1
}
