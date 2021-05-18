package homework8.views

import homework8.Styles
import homework8.controllers.GameScope
import homework8.games.RandomBotGame
import homework8.games.StrategicBotGame
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.find
import tornadofx.label
import tornadofx.seconds
import tornadofx.useMaxWidth
import tornadofx.vbox

class BotDifficultyView : View("Play with a bot") {
    override val root = vbox {
        addClass(Styles.window)
        addClass(Styles.menu)

        label("Select difficulty:")

        button("Easy") {
            useMaxWidth = true
            action {
                val scope = GameScope(RandomBotGame())
                replaceWith(find<GameView>(scope), ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }

        button("Medium") {
            useMaxWidth = true
            action {
                val scope = GameScope(StrategicBotGame())
                replaceWith(find<GameView>(scope), ViewTransition.Slide(Styles.TRANSITION_DURATION.seconds))
            }
        }

        button("< Go back") {
            useMaxWidth = true
            action {
                replaceWith<MainMenuView>(
                    ViewTransition.Slide(
                        Styles.TRANSITION_DURATION.seconds,
                        ViewTransition.Direction.RIGHT
                    )
                )
            }
        }
    }
}
