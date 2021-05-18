package homework8.views

import homework8.Styles
import homework8.controllers.GameController
import homework8.games.Game
import homework8.graphics.CrossIcon
import homework8.graphics.NoughtIcon
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.FX
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindClass
import tornadofx.button
import tornadofx.find
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.plusAssign
import tornadofx.seconds
import tornadofx.stackpane
import tornadofx.text
import tornadofx.vbox
import tornadofx.vgrow
import tornadofx.visibleWhen

class GameView : View("In game") {
    private val controller: GameController by inject()
    private val gameGridView: GameGridView by inject()

    private val player1SelectedObservable =
        Bindings.createObjectBinding(
            {
                if (controller.turnProperty.value == Game.PlayerId.PLAYER_1) Styles.playerIconSelected
                else Styles.none
            },
            controller.turnProperty
        )
    private val player2SelectedObservable =
        Bindings.createObjectBinding(
            {
                if (controller.turnProperty.value == Game.PlayerId.PLAYER_2) Styles.playerIconSelected
                else Styles.none
            },
            controller.turnProperty
        )

    override val root = vbox {
        addClass(Styles.window)
        addClass(Styles.gameView)

        vbox {
            addClass(Styles.gameViewHeader)

            hbox {
                hbox {
                    hgrow = Priority.ALWAYS
                    label(controller.player1NameProperty) { addClass(Styles.playerTitle) }
                }

                hbox {
                    hgrow = Priority.ALWAYS
                    alignment = Pos.TOP_RIGHT
                    label(controller.player2NameProperty) { addClass(Styles.playerTitle) }
                }
            }

            hbox {
                hbox {
                    addClass(Styles.playerScore)
                    hgrow = Priority.ALWAYS
                    alignment = Pos.CENTER_LEFT

                    val icon = when (controller.delegate.getMark(Game.PlayerId.PLAYER_1)) {
                        Game.Mark.CROSS -> find<CrossIcon>()
                        Game.Mark.NOUGHT -> find<NoughtIcon>()
                    }
                    icon.root.bindClass(player1SelectedObservable)

                    this += icon

                    text(controller.player1ScoreProperty.asString())
                }
                hbox {
                    addClass(Styles.playerScore)
                    hgrow = Priority.ALWAYS
                    alignment = Pos.CENTER_RIGHT

                    val icon = when (controller.delegate.getMark(Game.PlayerId.PLAYER_2)) {
                        Game.Mark.CROSS -> find<CrossIcon>()
                        Game.Mark.NOUGHT -> find<NoughtIcon>()
                    }
                    icon.root.bindClass(player2SelectedObservable)

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
                        replaceWith(
                            find<MainMenuView>(FX.defaultScope),
                            ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds, ViewTransition.Direction.RIGHT)
                        )
                    }
                }

                hbox {
                    hgrow = Priority.ALWAYS
                }

                button("Play again") {
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
