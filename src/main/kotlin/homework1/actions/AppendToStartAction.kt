package homework1.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This action appends to the start of the list.
 */
@Serializable
@SerialName("appendToStart")
class AppendToStartAction(private val value: Int) : Action {
    override val name: String
        get() = "Append $value to start"

    override fun perform(list: MutableList<Int>) {
        list.add(0, value)
    }

    override fun undo(list: MutableList<Int>) {
        list.removeFirst()
    }
}
