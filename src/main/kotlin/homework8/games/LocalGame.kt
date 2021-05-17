package homework8.games

import homework8.players.Player

class LocalGame : Game() {
    override val player1 = Player(Delegate(this, PlayerPos.PLAYER_1))
    override val player2 = Player(Delegate(this, PlayerPos.PLAYER_2))
}
