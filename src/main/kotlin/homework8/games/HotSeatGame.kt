package homework8.games

import homework8.players.ControlledPlayer

class HotSeatGame : DualControlGame() {
    override val player1 = ControlledPlayer(Delegate(this, PlayerId.PLAYER_1))
    override val player2 = ControlledPlayer(Delegate(this, PlayerId.PLAYER_2))
    override val controlledPlayer1
        get() = this.player1
    override val controlledPlayer2
        get() = this.player2
}
