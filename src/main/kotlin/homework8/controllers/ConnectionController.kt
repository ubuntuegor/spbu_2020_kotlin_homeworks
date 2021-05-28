@file:Suppress("NoWildcardImports", "WildcardImport")

package homework8.controllers

import homework8.Styles
import homework8.games.OnlineGame
import homework8.server.events.ClientToServer
import homework8.server.events.Connected
import homework8.server.events.CreateRoom
import homework8.server.events.GameCreated
import homework8.server.events.JoinRoom
import homework8.server.events.RoomsList
import homework8.server.events.ServerToClient
import homework8.views.ErrorModal
import homework8.views.GameView
import homework8.views.MainMenuView
import homework8.views.RoomSelectionView
import homework8.views.RoomWaitingView
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.StageStyle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tornadofx.Controller
import tornadofx.ViewTransition
import tornadofx.find
import tornadofx.runLater
import tornadofx.seconds
import java.net.ConnectException

class ConnectionController : Controller() {
    private enum class State { IN_LOBBY, IN_ROOM }

    private lateinit var state: State

    private val client = HttpClient {
        install(WebSockets)
    }

    var session: DefaultClientWebSocketSession? = null

    var playerId = SimpleIntegerProperty()
    val rooms = SimpleObjectProperty(listOf<Int>())

    private suspend fun DefaultWebSocketSession.sendEvent(event: ClientToServer) =
        send(Json.encodeToString(event))

    private fun Frame.Text.readEvent() = Json.decodeFromString<ServerToClient>(this.readText())

    fun createRoom() {
        GlobalScope.launch { session?.sendEvent(CreateRoom) }
        rooms.set(listOf())
        state = State.IN_ROOM
    }

    fun joinRoom(id: Int) {
        GlobalScope.launch { session?.sendEvent(JoinRoom(id)) }
        rooms.set(listOf())
    }

    fun connect() {
        GlobalScope.launch {
            try {
                client.ws(SERVER_URI) {
                    state = State.IN_LOBBY
                    session = this
                    runLater { find<RoomSelectionView>().root.isDisable = false }

                    val sendChannel = Channel<ServerToClient>()
                    val receiveChannel = Channel<ClientToServer>()
                    var game: OnlineGame? = null

                    for (frame in incoming) {
                        when (val event = (frame as Frame.Text).readEvent()) {
                            is Connected -> runLater { playerId.set(event.id) }
                            is RoomsList -> runLater { rooms.set(event.rooms) }
                            is GameCreated -> {
                                game = OnlineGame(receiveChannel, sendChannel, event.mark)
                                break
                            }
                        }
                    }

                    if (game != null) {
                        runLater {
                            val scope = GameViewScope(game.controlledPlayer1)
                            when (state) {
                                State.IN_LOBBY -> find<RoomSelectionView>()
                                State.IN_ROOM -> find<RoomWaitingView>()
                            }.replaceWith(
                                find<GameView>(scope),
                                ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds)
                            )
                        }

                        GlobalScope.launch {
                            for (event in receiveChannel) {
                                sendEvent(event)
                            }
                            close()
                        }

                        for (frame in incoming) {
                            sendChannel.send((frame as Frame.Text).readEvent())
                        }
                    }

                    sendChannel.close()
                    receiveChannel.close()
                }
            } catch (e: ConnectException) {
                runLater {
                    find<ErrorModal>(mapOf(ErrorModal::message to "Can't connect to the game server!")).openModal(
                        StageStyle.UTILITY
                    )
                    find<RoomSelectionView>().replaceWith<MainMenuView>(
                        ViewTransition.Slide(
                            Styles.TRANSITION_DURATION.seconds,
                            ViewTransition.Direction.RIGHT
                        )
                    )
                }
            }
        }
    }

    fun disconnect() {
        GlobalScope.launch { session?.close() }
    }

    companion object {
        private const val SERVER_URI = "ws://localhost:8080/game"
    }
}
