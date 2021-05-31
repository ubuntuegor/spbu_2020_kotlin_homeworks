package homework8.games

import homework8.games.basic.Cell
import homework8.games.basic.Game
import homework8.games.basic.Mark
import homework8.games.basic.PlayerData
import homework8.games.basic.PlayerId
import homework8.players.ControlledPlayer
import homework8.server.events.ClientToServer
import homework8.server.events.MakeMove
import homework8.server.events.OnGameResult
import homework8.server.events.OnMoveRequested
import homework8.server.events.OnOpponentLeft
import homework8.server.events.OnOpponentMove
import homework8.server.events.OnStart
import homework8.server.events.Ready
import homework8.server.events.ServerToClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class OnlineGame(
    private val sendChannel: SendChannel<ClientToServer>,
    private val receiveChannel: ReceiveChannel<ServerToClient>,
    mark: Mark
) : Game() {
    override var turn = if (mark == Mark.CROSS) PlayerId.PLAYER_1 else PlayerId.PLAYER_2

    val controlledPlayer1 = ControlledPlayer(Delegate(PlayerId.PLAYER_1))
    override val player1Data = PlayerData(mark, "You")
    override val player2Data = PlayerData(mark.other(), "Opponent")

    override fun onReady(playerId: PlayerId) {
        GlobalScope.launch { sendChannel.send(Ready) }
    }

    override fun onMakeMove(playerId: PlayerId, cell: Cell) {
        field.setCell(cell, playerId)
        turn = PlayerId.PLAYER_2
        GlobalScope.launch { sendChannel.send(MakeMove(cell)) }
    }

    override fun onQuit(playerId: PlayerId) {
        sendChannel.close()
    }

    init {
        GlobalScope.launch {
            for (event in receiveChannel) {
                when (event) {
                    is OnStart -> {
                        field.clear()
                        controlledPlayer1.onStart()
                    }
                    is OnMoveRequested -> {
                        turn = PlayerId.PLAYER_1
                        controlledPlayer1.onMoveRequested()
                    }
                    is OnOpponentMove -> {
                        field.setCell(event.cell, PlayerId.PLAYER_2)
                        controlledPlayer1.onOpponentMove(event.cell)
                    }
                    is OnGameResult -> {
                        controlledPlayer1.onGameResult(event.winner)
                    }
                    is OnOpponentLeft -> {
                        controlledPlayer1.onOpponentLeft()
                    }
                    else -> {
                    }
                }
            }
            controlledPlayer1.onOpponentLeft()
        }
    }
}
