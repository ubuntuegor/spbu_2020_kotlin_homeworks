package homework8.controllers

import homework8.games.Game
import tornadofx.Scope

class GameScope(val game: Game) : Scope()
