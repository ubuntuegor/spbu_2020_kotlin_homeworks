package homework8.controllers

import homework8.players.ControlledPlayer
import tornadofx.Scope

class GameViewScope(val player1: ControlledPlayer, val player2: ControlledPlayer? = null) : Scope()
