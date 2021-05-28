package homework8.views

import homework8.Styles
import homework8.controllers.ConnectionController
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.FX
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.clear
import tornadofx.find
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.onChange
import tornadofx.plusAssign
import tornadofx.scrollpane
import tornadofx.seconds
import tornadofx.useMaxWidth
import tornadofx.vbox
import tornadofx.vgrow

class RoomSelectionView : View("Choose a room") {
    private val controller: ConnectionController by inject()

    lateinit var roomsList: VBox

    override val root = vbox {
        addClass(Styles.window)
        addClass(Styles.menu)

        isDisable = true

        button("Create room") {
            useMaxWidth = true
            action {
                controller.createRoom()
                replaceWith<RoomWaitingView>(ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }

        scrollpane {
            vgrow = Priority.ALWAYS
            isFitToWidth = true

            roomsList = vbox {}
        }

        hbox {
            alignment = Pos.BOTTOM_LEFT
            button("Quit") {
                action {
                    controller.disconnect()
                    replaceWith(
                        find<MainMenuView>(FX.defaultScope),
                        ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds, ViewTransition.Direction.RIGHT)
                    )
                }
            }
        }
    }

    override fun onDock() {
        controller.rooms.onChange {
            roomsList.clear()
            it?.forEach { id ->
                roomsList += hbox {
                    addClass(Styles.roomFragment)
                    alignment = Pos.CENTER

                    label("Room $id")
                    hbox { hgrow = Priority.ALWAYS }
                    button("Join") {
                        action {
                            root.isDisable = true
                            controller.joinRoom(id)
                        }
                    }
                }
            }
        }

        controller.connect()
    }
}
