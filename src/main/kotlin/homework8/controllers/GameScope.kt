package homework8.controllers

import homework8.games.OneControlledPlayerGame
import tornadofx.Scope

class GameScope(val game: OneControlledPlayerGame) : Scope()
