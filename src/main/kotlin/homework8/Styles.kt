@file:Suppress("MagicNumber")

package homework8

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.multi
import tornadofx.percent
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val none by cssclass()

        val window by cssclass()
        val menu by cssclass()
        val gameView by cssclass()
        val gameViewHeader by cssclass()

        val gameCell by cssclass()
        val firstRowCell by cssclass()
        val firstColumnCell by cssclass()

        val playerTitle by cssclass()
        val playerScore by cssclass()
        val playerIcon by cssclass()
        val playerIconSelected by cssclass()

        val messageLabel by cssclass()

        const val WINDOW_WIDTH = 500.0
        const val WINDOW_HEIGHT = 680.0
        const val TRANSITION_DURATION = 0.2

        val CROSSES_COLOR: Color = Color.web("539FE5")
        val NOUGHTS_COLOR: Color = Color.web("ED5545")

        const val GAME_CELL_SIZE = 140.0
        const val GAME_CELL_PADDING = 30.0
        const val GAME_CELL_STROKE_WIDTH = 18.0
        val GAME_CELL_BORDER_COLOR: Color = Color.LIGHTGRAY
        const val GAME_CELL_BORDER_WIDTH = 2
        const val ANIMATION_DURATION = 0.8

        const val PLAYER_ICON_SIZE = 50.0
        const val PLAYER_ICON_PADDING = 12.0
        const val PLAYER_ICON_STROKE_WIDTH = 7.0
        const val PLAYER_ICON_BORDER_WIDTH = 5.0

        fun getFont(weight: FontWeight, size: Double): Font {
            val urls = mapOf(
                FontWeight.NORMAL to "fonts/Inter-Regular.otf",
                FontWeight.SEMI_BOLD to "fonts/Inter-SemiBold.otf",
                FontWeight.MEDIUM to "fonts/Inter-Medium.otf",
                FontWeight.BOLD to "fonts/Inter-Bold.otf"
            )
            val url = urls[weight]
            val resourcePath = if (url != null) this::class.java.getResource(url)?.toExternalForm() else null
            val font = if (resourcePath != null) Font.loadFont(resourcePath, size) else null
            return font ?: Font.font(null, weight, size)
        }
    }

    init {
        button {
            font = getFont(FontWeight.NORMAL, 14.0)
        }

        window {
            backgroundColor += Color.web("fafafa")
            padding = box(20.px)
        }

        menu {
            spacing = 15.px
            alignment = Pos.CENTER

            label {
                font = getFont(FontWeight.SEMI_BOLD, 14.0)
            }
        }

        gameCell {
            minWidth = GAME_CELL_SIZE.px
            minHeight = GAME_CELL_SIZE.px
            maxWidth = GAME_CELL_SIZE.px
            maxHeight = GAME_CELL_SIZE.px

            borderColor += box(GAME_CELL_BORDER_COLOR, Color.TRANSPARENT, Color.TRANSPARENT, GAME_CELL_BORDER_COLOR)
            borderWidth = multi(box(GAME_CELL_BORDER_WIDTH.px))

            and(firstColumnCell) {
                borderWidth = multi(box(GAME_CELL_BORDER_WIDTH.px, 0.px))
            }
            and(firstRowCell) {
                borderWidth = multi(box(0.px, GAME_CELL_BORDER_WIDTH.px))
                and(firstColumnCell) {
                    borderWidth = multi(box(0.px))
                }
            }
            and(hover) {
                backgroundColor += Color.web("dddddd")
            }
        }

        gameView {
            spacing = 20.px

            gameViewHeader {
                spacing = 5.px

                playerTitle {
                    font = getFont(FontWeight.BOLD, 18.0)
                }

                playerScore {
                    font = getFont(FontWeight.NORMAL, 26.0)
                    spacing = 7.px
                }

                playerIcon {
                    minWidth = (PLAYER_ICON_SIZE + PLAYER_ICON_BORDER_WIDTH * 2).px
                    minHeight = (PLAYER_ICON_SIZE + PLAYER_ICON_BORDER_WIDTH * 2).px
                    maxWidth = (PLAYER_ICON_SIZE + PLAYER_ICON_BORDER_WIDTH * 2).px
                    maxHeight = (PLAYER_ICON_SIZE + PLAYER_ICON_BORDER_WIDTH * 2).px

                    and(playerIconSelected) {

                        borderWidth += box(PLAYER_ICON_BORDER_WIDTH.px)
                        borderColor += box(Color.web("48f179"))
                        borderRadius += box(50.percent)
                    }
                }
            }
        }

        messageLabel {
            font = getFont(FontWeight.MEDIUM, 14.0)
            padding = box(6.px, 0.px)
        }
    }
}
