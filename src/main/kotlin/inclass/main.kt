@file:Suppress("NoWildcardImports", "WildcardImport")

package inclass

import tornadofx.*

private const val GRID_SIZE = 3

class GameWindow : View("Tic-Tac-Toe") {
    override val root = gridpane {
        repeat(GRID_SIZE) {
            row {
                repeat(GRID_SIZE) {
                    button("o") {
                        action {
                            this.text = "x"
                        }
                    }
                }
            }
        }
    }
}

class Game : App(GameWindow::class)

fun main(args: Array<String>) {
    launch<Game>(args)
}
