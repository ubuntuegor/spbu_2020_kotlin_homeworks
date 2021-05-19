package homework8.views

import homework8.games.Game
import homework8.controllers.GameController
import homework8.Styles
import javafx.geometry.Pos
import tornadofx.View
import tornadofx.addClass
import tornadofx.enableWhen
import tornadofx.gridpane
import tornadofx.onLeftClick
import tornadofx.plusAssign
import tornadofx.row

class GameGridView : View() {
    private val controller: GameController by inject()

    override val root = gridpane {
        alignment = Pos.CENTER
        enableWhen(controller.enableFieldProperty)
    }

    init {
        val gameSize = controller.delegate.size
        with(root) {
            repeat(gameSize) { y ->
                row {
                    repeat(gameSize) { x ->
                        val gameCell =
                            find<GameCellFragment>(mapOf(GameCellFragment::markProperty to controller.localField[y][x]))
                        gameCell.apply {
                            if (x == 0) this.root.addClass(Styles.firstColumnCell)
                            if (y == 0) this.root.addClass(Styles.firstRowCell)
                            this.root.onLeftClick {
                                controller.makeMove(Game.Cell(x, y))
                            }
                        }

                        this += gameCell
                    }
                }
            }
        }
    }
}
