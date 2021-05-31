@file:Suppress("NoWildcardImports", "WildcardImport")

package homework8.server

import homework8.games.HotSeatGame
import homework8.games.basic.PlayerId
import homework8.players.ControlledPlayer
import homework8.server.events.ClientToServer
import homework8.server.events.Connected
import homework8.server.events.CreateRoom
import homework8.server.events.GameCreated
import homework8.server.events.JoinRoom
import homework8.server.events.MakeMove
import homework8.server.events.OnGameResult
import homework8.server.events.OnMoveRequested
import homework8.server.events.OnOpponentLeft
import homework8.server.events.OnOpponentMove
import homework8.server.events.OnStart
import homework8.server.events.Ready
import homework8.server.events.RoomsList
import homework8.server.events.ServerToClient
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class User(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = lastId.getAndIncrement()
}

class Room(val creator: User) {
    val game = HotSeatGame()
}

suspend fun DefaultWebSocketSession.sendEvent(event: ServerToClient) =
    send(Json.encodeToString(event))

fun Frame.Text.readEvent() = Json.decodeFromString<ClientToServer>(this.readText())

suspend fun DefaultWebSocketSession.play(user: User, player: ControlledPlayer) {
    player.init(
        onStart = { GlobalScope.launch { user.session.sendEvent(OnStart) } },
        onMoveRequested = { GlobalScope.launch { user.session.sendEvent(OnMoveRequested) } },
        onOpponentMove = { GlobalScope.launch { user.session.sendEvent(OnOpponentMove(it)) } },
        onGameResult = { winner ->
            val localWinner = if (player.delegate.playerId == PlayerId.PLAYER_2) winner?.other() else winner
            GlobalScope.launch { user.session.sendEvent(OnGameResult(localWinner)) }
        },
        onOpponentLeft = { GlobalScope.launch { user.session.sendEvent(OnOpponentLeft) } }
    )

    for (frame in incoming) {
        frame as Frame.Text
        when (val event = frame.readEvent()) {
            is Ready -> player.delegate.ready()
            is MakeMove -> player.delegate.makeMove(event.cell)
        }
    }
    try {
        player.delegate.quit()
    } catch (e: UninitializedPropertyAccessException) {
        println("Closing room ${user.id} without a started game.")
    }
}

fun Application.module() {
    install(WebSockets)
    routing {
        val lobby = Collections.synchronizedSet<User>(LinkedHashSet())
        val rooms = Collections.synchronizedMap<Int, Room>(LinkedHashMap())

        suspend fun broadcastRooms() {
            lobby.forEach {
                it.session.sendEvent(RoomsList(rooms.keys.toList()))
            }
        }

        suspend fun DefaultWebSocketSession.selectRoom(user: User): ControlledPlayer? {
            var player: ControlledPlayer? = null
            for (frame in incoming) {
                frame as Frame.Text
                when (val event = frame.readEvent()) {
                    is CreateRoom -> {
                        lobby.remove(user)
                        val newRoom = Room(user)
                        rooms[user.id] = newRoom
                        broadcastRooms()
                        player = newRoom.game.controlledPlayer1
                    }
                    is JoinRoom -> {
                        lobby.remove(user)
                        val room = rooms.remove(event.id)
                            ?: throw IllegalStateException("User ${user.id} tried joining non-existing room " +
                                    "${event.id}")
                        broadcastRooms()
                        val creatorMark = with(room.game.controlledPlayer1.delegate) { getPlayerData(playerId).mark }
                        room.creator.session.sendEvent(GameCreated(creatorMark))
                        sendEvent(GameCreated(creatorMark.other()))
                        player = room.game.controlledPlayer2
                    }
                }
                if (player != null) break
            }
            return player
        }

        webSocket("/game") {
            val user = User(this)
            lobby += user

            println("User ${user.id} connected.")

            sendEvent(Connected(user.id))
            sendEvent(RoomsList(rooms.keys.toList()))

            try {
                val player = selectRoom(user)
                if (player != null) play(user, player)
            } finally {
                lobby.remove(user)
                rooms.remove(user.id)
                broadcastRooms()
                println("User ${user.id} disconnected.")
            }
        }
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
