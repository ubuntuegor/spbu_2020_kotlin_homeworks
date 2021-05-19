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
import tornadofx.booleanBinding
import tornadofx.button
import tornadofx.find
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.plusAssign
import tornadofx.seconds
import tornadofx.stackpane
import tornadofx.stringBinding
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
                if (controller.stateProperty.value == GameController.State.PLAYER_1_MOVE) Styles.playerIconSelected
                else Styles.none
            },
            controller.stateProperty
        )
    private val player2SelectedObservable =
        Bindings.createObjectBinding(
            {
                if (controller.stateProperty.value == GameController.State.PLAYER_2_MOVE) Styles.playerIconSelected
                else Styles.none
            },
            controller.stateProperty
        )

    override val root = vbox {
        addClass(Styles.window)
        addClass(Styles.gameView)

        vbox {
            addClass(Styles.gameViewHeader)

            hbox {
                addClass(Styles.playerTitle)

                hbox {
                    hgrow = Priority.ALWAYS
                    label(controller.player1NameProperty)
                }

                hbox {
                    hgrow = Priority.ALWAYS
                    alignment = Pos.TOP_RIGHT
                    label(controller.player2NameProperty)
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
                label(controller.stateProperty.stringBinding {
                    when (it) {
                        GameController.State.READY -> "Waiting for opponent..."
                        GameController.State.NOUGHTS_WON -> "Noughts won!"
                        GameController.State.CROSSES_WON -> "Crosses won!"
                        GameController.State.TIE -> "It's a tie!"
                        GameController.State.ENDED -> "Opponent has left."
                        else -> null
                    }
                })
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
                    visibleWhen(controller.stateProperty.booleanBinding {
                        it in listOf(
                            GameController.State.CROSSES_WON,
                            GameController.State.NOUGHTS_WON,
                            GameController.State.TIE
                        )
                    })
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
