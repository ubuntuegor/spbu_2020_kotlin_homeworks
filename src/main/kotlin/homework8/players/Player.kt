package homework8.players

import homework8.games.Game

open class Player(open val delegate: Game.Delegate) {
    open var onStart: () -> Unit = {}
    open var onMoveRequested: () -> Unit = {}
    open var onOpponentMove: (Game.Cell, Game.PlayerPos) -> Unit = { _, _ -> }
    open var onGameResult: (Game.PlayerPos?) -> Unit = { _ -> }
    open var onOpponentLeft: () -> Unit = {}
}
