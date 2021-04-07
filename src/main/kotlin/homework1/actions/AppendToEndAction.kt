package homework1.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This action appends to the end of the list.
 */
@Serializable
@SerialName("appendToEnd")
class AppendToEndAction<T>(private val value: T) : Action<T> {
    override val name: String
        get() = "Append $value to end"

    override fun perform(list: MutableList<T>) {
        list.add(value)
    }

    override fun undo(list: MutableList<T>) {
        list.removeLast()
    }
}
