package homework1.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("appendToEnd")
class AppendToEndAction(private val value: Int) : Action {
    override val name: String
        get() = "Append $value to end"

    override fun perform(list: MutableList<Int>) {
        list.add(value)
    }

    override fun undo(list: MutableList<Int>) {
        list.removeLast()
    }
}
