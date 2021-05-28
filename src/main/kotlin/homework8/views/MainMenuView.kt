package homework8.views

import homework8.Styles
import homework8.controllers.GameViewScope
import homework8.games.HotSeatGame
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.find
import tornadofx.imageview
import tornadofx.seconds
import tornadofx.stackpane
import tornadofx.useMaxWidth
import tornadofx.vbox

class MainMenuView : View("Main menu") {
    override val root = vbox {
        addClass(Styles.window)
        addClass(Styles.menu)

        val imagePane = stackpane { useMaxWidth = true }
        imageview(resources["logo.png"]) {
            isPreserveRatio = true
            fitWidthProperty().bind(imagePane.widthProperty())
        }

        button("Play on one computer") {
            useMaxWidth = true
            action {
                val game = HotSeatGame()
                val scope = GameViewScope(game.controlledPlayer1, game.controlledPlayer2)
                replaceWith(find<GameView>(scope), ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }
        button("Play with bot") {
            useMaxWidth = true
            action {
                replaceWith<BotDifficultyView>(ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }
        button("Play online") {
            useMaxWidth = true
            action {
                replaceWith<RoomSelectionView>(ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }
    }

    init {
        primaryStage.isResizable = false
        primaryStage.width = Styles.WINDOW_WIDTH
        primaryStage.height = Styles.WINDOW_HEIGHT
    }
}
