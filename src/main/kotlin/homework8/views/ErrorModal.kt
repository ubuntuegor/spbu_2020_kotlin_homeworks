package homework8.views

import homework8.Styles
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.label
import tornadofx.vbox

class ErrorModal : Fragment("Error") {
    val message: String by param()

    override val root = vbox {
        addClass(Styles.errorFragment)

        label("Error:") { addClass(Styles.title) }
        label(message)
    }
}
