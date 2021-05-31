package homework8.graphics

import homework8.Styles
import javafx.animation.Interpolator
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import tornadofx.Fragment
import tornadofx.arc
import tornadofx.group
import tornadofx.keyframe
import tornadofx.rectangle
import tornadofx.seconds
import tornadofx.stackpane
import tornadofx.timeline

class AnimatedNought : Fragment() {
    override val root = stackpane {
        group {
            rectangle(0, 0, Styles.GAME_CELL_SIZE, Styles.GAME_CELL_SIZE) {
                fill = Color.TRANSPARENT
            }
            val center = Styles.GAME_CELL_SIZE / 2
            val radius = center - Styles.GAME_CELL_PADDING
            arc(center, center, radius, radius, START_ANGLE, 0) {
                stroke = Styles.NOUGHTS_COLOR
                fill = Color.TRANSPARENT
                strokeWidth = Styles.GAME_CELL_STROKE_WIDTH
                strokeLineCap = StrokeLineCap.ROUND

                timeline {
                    keyframe(Styles.ANIMATION_DURATION.seconds) {
                        keyvalue(lengthProperty(), LENGTH, Interpolator.EASE_OUT)
                    }
                }
            }
        }
    }

    companion object {
        private const val START_ANGLE = 90.0
        private const val LENGTH = 360.0
    }
}
