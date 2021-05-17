package homework8.views

import homework8.Styles
import homework8.controllers.GameScope
import homework8.games.GameWithBot
import homework8.games.LocalGame
import javafx.scene.Parent
import tornadofx.View

class MainView : View("Noughts and crosses") {
    override lateinit var root: Parent

    init {
        primaryStage.isResizable = false
        primaryStage.width = Styles.WINDOW_WIDTH
        primaryStage.height = Styles.WINDOW_HEIGHT

        val scope = GameScope(GameWithBot())
        root = tornadofx.find<GameView>(scope).root
    }
}
