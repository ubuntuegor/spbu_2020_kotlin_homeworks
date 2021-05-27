package homework8.games

import homework8.games.basic.Mark
import homework8.games.basic.PlayerData
import homework8.games.basic.PlayerId
import homework8.players.ControlledPlayer

class HotSeatGame : LocalGame() {
    override val player1 = ControlledPlayer(Delegate(PlayerId.PLAYER_1))
    override val player2 = ControlledPlayer(Delegate(PlayerId.PLAYER_2))
    override val player1Data = PlayerData(Mark.CROSS, "Player 1")
    override val player2Data = PlayerData(Mark.NOUGHT, "Player 2")
    val controlledPlayer1
        get() = this.player1
    val controlledPlayer2
        get() = this.player2
}
