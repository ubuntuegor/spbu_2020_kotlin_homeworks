package homework8.views

import homework8.Styles
import homework8.controllers.GameController
import homework8.games.Game
import homework8.graphics.CrossIcon
import homework8.graphics.NoughtIcon
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindClass
import tornadofx.button
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.plusAssign
import tornadofx.stackpane
import tornadofx.text
import tornadofx.vbox
import tornadofx.vgrow
import tornadofx.visibleWhen

class GameView : View() {
    private val controller: GameController by inject()
    private val gameGridView: GameGridView by inject()

    private val bind1 =
        Bindings.createObjectBinding(
            {
                if (controller.turnProperty.value == Game.PlayerPos.PLAYER_1) Styles.playerIconSelected
                else Styles.none
            },
            controller.turnProperty
        )
    private val bind2 =
        Bindings.createObjectBinding(
            {
                if (controller.turnProperty.value == Game.PlayerPos.PLAYER_2) Styles.playerIconSelected
                else Styles.none
            },
            controller.turnProperty
        )

    override val root = vbox {
        addClass(Styles.gameView)

        vbox {
            addClass(Styles.gameViewHeader)

            hbox {
                hbox {
                    hgrow = Priority.ALWAYS
                    label("You") { addClass(Styles.playerTitle) }
                }

                hbox {
                    hgrow = Priority.ALWAYS
                    alignment = Pos.TOP_RIGHT
                    label("Opponent") { addClass(Styles.playerTitle) }
                }
            }

            hbox {
                hbox {
                    addClass(Styles.playerScore)
                    hgrow = Priority.ALWAYS
                    alignment = Pos.CENTER_LEFT

                    val icon = when (controller.delegate.getMark(Game.PlayerPos.PLAYER_1)) {
                        Game.Mark.CROSS -> find<CrossIcon>()
                        Game.Mark.NOUGHT -> find<NoughtIcon>()
                    }
                    icon.root.bindClass(bind1)

                    this += icon

                    text(controller.player1ScoreProperty.asString())
                }
                hbox {
                    addClass(Styles.playerScore)
                    hgrow = Priority.ALWAYS
                    alignment = Pos.CENTER_RIGHT

                    val icon = when (controller.delegate.getMark(Game.PlayerPos.PLAYER_2)) {
                        Game.Mark.CROSS -> find<CrossIcon>()
                        Game.Mark.NOUGHT -> find<NoughtIcon>()
                    }
                    icon.root.bindClass(bind2)

                    text(controller.player2ScoreProperty.asString())

                    this += icon
                }
            }
        }

        this += gameGridView

        stackpane {
            vgrow = Priority.ALWAYS
            alignment = Pos.BOTTOM_CENTER

            vbox {
                addClass(Styles.messageLabel)
                alignment = Pos.BOTTOM_CENTER
                label(controller.messageProperty)
            }

            hbox {
                alignment = Pos.BOTTOM_CENTER
                button("Quit") {
                    action {
                        close()
                    }
                }

                hbox {
                    hgrow = Priority.ALWAYS
                }

                button("Restart") {
                    visibleWhen(controller.enableRestartProperty)
                    action {
                        controller.ready()
                    }
                }
            }
        }
    }

    override fun onUndock() {
        controller.quit()
        super.onUndock()
    }
}
