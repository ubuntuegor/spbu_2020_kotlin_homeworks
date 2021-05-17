package homework8.views

import homework8.games.Game
import homework8.Styles
import javafx.beans.property.SimpleObjectProperty
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.clear
import tornadofx.enableWhen
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.plusAssign
import tornadofx.setValue
import tornadofx.stackpane

class GameCellFragment : Fragment() {
    val markProperty: SimpleObjectProperty<Game.Mark> by param()
    private var mark: Game.Mark? by markProperty

    override var root = stackpane {
        addClass(Styles.gameCell)
        enableWhen(markProperty.isNull)
    }

    init {
        markProperty.onChange {
            root.clear()
            when (mark) {
                Game.Mark.CROSS -> root += find<homework8.graphics.AnimatedCross>()
                Game.Mark.NOUGHT -> root += find<homework8.graphics.AnimatedNought>()
                null -> {
                }
            }
        }
    }
}
