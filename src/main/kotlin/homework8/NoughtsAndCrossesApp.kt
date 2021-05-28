package homework8

import homework8.views.MainMenuView
import tornadofx.App

class NoughtsAndCrossesApp : App(MainMenuView::class, Styles::class) /* {
    private val controller: ConnectionController by inject()

    override fun stop() {
        controller.disconnect()
    }
} */
