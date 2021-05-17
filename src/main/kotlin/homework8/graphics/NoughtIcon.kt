package homework8.graphics

import homework8.Styles
import javafx.scene.paint.Color
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.circle
import tornadofx.group
import tornadofx.rectangle
import tornadofx.stackpane

class NoughtIcon : Fragment() {
    override val root = stackpane {
        addClass(Styles.playerIcon)
        group {
            rectangle(0, 0, Styles.PLAYER_ICON_SIZE, Styles.PLAYER_ICON_SIZE) {
                fill = Color.TRANSPARENT
            }
            val center = Styles.PLAYER_ICON_SIZE / 2
            val radius = center - Styles.PLAYER_ICON_PADDING
            circle(center, center, radius) {
                stroke = Styles.NOUGHTS_COLOR
                fill = Color.TRANSPARENT
                strokeWidth = Styles.PLAYER_ICON_STROKE_WIDTH
            }
        }
    }
}
