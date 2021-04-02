package homework1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes an action which can be applied to a list of integers.
 */
@Serializable
sealed class Action<T> {
    /**
     * A descriptive name of this action.
     */
    abstract val name: String

    /**
     * Applies the action.
     * @param list list which the action will be applied to.
     */
    abstract fun perform(list: MutableList<T>)

    /**
     * Reverts the changes done by [perform], assuming that the action was the last one applied to the list.
     * @param list list to revert the action in.
     */
    abstract fun undo(list: MutableList<T>)
}

/**
 * This action appends to the start of the list.
 */
@Serializable
@SerialName("appendToStart")
class AppendToStartAction<T>(private val value: T) : Action<T>() {
    override val name: String
        get() = "Append $value to start"

    override fun perform(list: MutableList<T>) {
        list.add(0, value)
    }

    override fun undo(list: MutableList<T>) {
        list.removeFirst()
    }
}

/**
 * This action appends to the end of the list.
 */
@Serializable
@SerialName("appendToEnd")
class AppendToEndAction<T>(private val value: T) : Action<T>() {
    override val name: String
        get() = "Append $value to end"

    override fun perform(list: MutableList<T>) {
        list.add(value)
    }

    override fun undo(list: MutableList<T>) {
        list.removeLast()
    }
}

/**
 * This action moves an element within a list.
 */
@Serializable
@SerialName("moveElement")
class MoveElementAction<T>(private val indexFrom: Int, private val indexTo: Int) : Action<T>() {
    private fun MutableList<T>.moveElement(from: Int, to: Int) {
        if (from !in this.indices) throw IllegalArgumentException("Can't move: element $from doesn't exist")
        if (to !in this.indices) throw IllegalArgumentException("Can't move: position $to is not available")
        val temp = this.removeAt(from)
        this.add(to, temp)
    }

    override val name: String
        get() = "Move element from index $indexFrom to $indexTo"

    override fun perform(list: MutableList<T>) {
        list.moveElement(indexFrom, indexTo)
    }

    override fun undo(list: MutableList<T>) {
        list.moveElement(indexTo, indexFrom)
    }
}
