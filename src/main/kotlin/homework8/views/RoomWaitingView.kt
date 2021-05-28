package homework8.views

import homework8.Styles
import homework8.controllers.ConnectionController
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.FX
import tornadofx.label
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.hbox
import tornadofx.seconds
import tornadofx.stringBinding
import tornadofx.vbox
import tornadofx.vgrow

class RoomWaitingView : View() {
    private val controller: ConnectionController by inject()

    override val root = vbox {
        addClass(Styles.roomWaitingView)
        addClass(Styles.window)

        vbox {
            vgrow = Priority.ALWAYS
            alignment = Pos.CENTER
            label(controller.playerId.stringBinding { "Room $it" }) { addClass(Styles.title) }
            label("Waiting for another player to join...")
        }

        hbox {
            alignment = Pos.BOTTOM_LEFT
            button("Quit") {
                action {
                    controller.disconnect()
                    replaceWith(
                        tornadofx.find<MainMenuView>(FX.defaultScope),
                        ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds, ViewTransition.Direction.RIGHT)
                    )
                }
            }
        }
    }
}
