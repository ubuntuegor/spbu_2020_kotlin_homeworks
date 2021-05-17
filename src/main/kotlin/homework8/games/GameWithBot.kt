package homework8.games

import homework8.players.BotRandom
import homework8.players.Player

class GameWithBot : Game() {
    override val player1 = Player(Delegate(this, PlayerPos.PLAYER_1))
    override val player2 = BotRandom(Delegate(this, PlayerPos.PLAYER_2))
}
