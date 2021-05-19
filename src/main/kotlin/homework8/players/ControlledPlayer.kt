package homework8.players

import homework8.games.Game

class ControlledPlayer(override val delegate: Game.Delegate) : Player {
    private var isInitialized = false

    private lateinit var _onStart: () -> Unit
    private lateinit var _onMoveRequested: () -> Unit
    private lateinit var _onOpponentMove: (Game.Cell) -> Unit
    private lateinit var _onGameResult: (Game.PlayerId?) -> Unit
    private lateinit var _onOpponentLeft: () -> Unit

    override fun onStart() = _onStart()
    override fun onMoveRequested() = _onMoveRequested()
    override fun onOpponentMove(cell: Game.Cell) = _onOpponentMove(cell)
    override fun onGameResult(winner: Game.PlayerId?) = _onGameResult(winner)
    override fun onOpponentLeft() = _onOpponentLeft()

    fun init(
        onStart: () -> Unit = {},
        onMoveRequested: () -> Unit = {},
        onOpponentMove: (Game.Cell) -> Unit = { _ -> },
        onGameResult: (Game.PlayerId?) -> Unit = { _ -> },
        onOpponentLeft: () -> Unit = {}
    ) {
        if (isInitialized) return
        _onStart = onStart
        _onMoveRequested = onMoveRequested
        _onOpponentMove = onOpponentMove
        _onGameResult = onGameResult
        _onOpponentLeft = onOpponentLeft
        isInitialized = true
    }
}
