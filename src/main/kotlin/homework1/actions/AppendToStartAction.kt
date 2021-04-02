package homework1.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This action appends to the start of the list.
 */
@Serializable
@SerialName("appendToStart")
class AppendToStartAction<T>(private val value: T) : Action<T> {
    override val name: String
        get() = "Append $value to start"

    override fun perform(list: MutableList<T>) {
        list.add(0, value)
    }

    override fun undo(list: MutableList<T>) {
        list.removeFirst()
    }
}
