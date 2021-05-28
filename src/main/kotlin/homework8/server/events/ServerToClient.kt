package homework8.server.events

import homework8.games.basic.Cell
import homework8.games.basic.Mark
import homework8.games.basic.PlayerId
import kotlinx.serialization.Serializable

@Serializable
sealed class ServerToClient

@Serializable
class Connected(val id: Int) : ServerToClient()

@Serializable
class RoomsList(val rooms: List<Int>) : ServerToClient()

@Serializable
class GameCreated(val mark: Mark) : ServerToClient()

@Serializable
object OnStart : ServerToClient()

@Serializable
object OnMoveRequested : ServerToClient()

@Serializable
class OnOpponentMove(val cell: Cell) : ServerToClient()

@Serializable
class OnGameResult(val winner: PlayerId?) : ServerToClient()

@Serializable
object OnOpponentLeft : ServerToClient()
