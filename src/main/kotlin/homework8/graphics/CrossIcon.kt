package homework8.graphics

import homework8.Styles
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.group
import tornadofx.line
import tornadofx.rectangle
import tornadofx.stackpane

class CrossIcon : Fragment() {
    override val root = stackpane {
        addClass(Styles.playerIcon)
        group {
            rectangle(0, 0, Styles.PLAYER_ICON_SIZE, Styles.PLAYER_ICON_SIZE) {
                fill = Color.TRANSPARENT
            }
            val start = Styles.PLAYER_ICON_PADDING
            val end = Styles.PLAYER_ICON_SIZE - Styles.PLAYER_ICON_PADDING
            line(start, start, end, end) {
                stroke = Styles.CROSSES_COLOR
                strokeWidth = Styles.PLAYER_ICON_STROKE_WIDTH
                strokeLineCap = StrokeLineCap.ROUND
            }
            line(start, end, end, start) {
                stroke = Styles.CROSSES_COLOR
                strokeWidth = Styles.PLAYER_ICON_STROKE_WIDTH
                strokeLineCap = StrokeLineCap.ROUND
            }
        }
    }
}
