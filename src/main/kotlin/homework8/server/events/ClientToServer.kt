package homework8.server.events

import homework8.games.basic.Cell
import kotlinx.serialization.Serializable

@Serializable
sealed class ClientToServer

@Serializable
object CreateRoom : ClientToServer()

@Serializable
class JoinRoom(val id: Int) : ClientToServer()

@Serializable
object Ready : ClientToServer()

@Serializable
class MakeMove(val cell: Cell) : ClientToServer()
