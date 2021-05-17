package homework8.graphics

import homework8.Styles
import javafx.animation.Interpolator
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import tornadofx.Fragment
import tornadofx.group
import tornadofx.keyframe
import tornadofx.line
import tornadofx.rectangle
import tornadofx.seconds
import tornadofx.stackpane
import tornadofx.timeline

class AnimatedCross : Fragment() {
    override val root = stackpane {
        group {
            rectangle(0, 0, Styles.GAME_CELL_SIZE, Styles.GAME_CELL_SIZE) {
                fill = Color.TRANSPARENT
            }
            val start = Styles.GAME_CELL_PADDING
            val end = Styles.GAME_CELL_SIZE - Styles.GAME_CELL_PADDING
            val firstLine = line(start, start, start, start) {
                stroke = Styles.CROSSES_COLOR
                strokeWidth = Styles.GAME_CELL_STROKE_WIDTH
                strokeLineCap = StrokeLineCap.ROUND
            }
            val secondLine = line(start, end, start, end) {
                stroke = Styles.CROSSES_COLOR
                strokeWidth = 0.0
                strokeLineCap = StrokeLineCap.ROUND
            }
            timeline {
                keyframe((Styles.ANIMATION_DURATION / 2).seconds) {
                    keyvalue(firstLine.endXProperty(), end, Interpolator.EASE_IN)
                    keyvalue(firstLine.endYProperty(), end, Interpolator.EASE_IN)
                    keyvalue(secondLine.endXProperty(), start, Interpolator.DISCRETE)
                    keyvalue(secondLine.endYProperty(), end, Interpolator.DISCRETE)
                    keyvalue(secondLine.strokeWidthProperty(), Styles.GAME_CELL_STROKE_WIDTH, Interpolator.DISCRETE)
                }
                keyframe(Styles.ANIMATION_DURATION.seconds) {
                    keyvalue(secondLine.endXProperty(), end, Interpolator.EASE_OUT)
                    keyvalue(secondLine.endYProperty(), start, Interpolator.EASE_OUT)
                }
            }
        }
    }
}
