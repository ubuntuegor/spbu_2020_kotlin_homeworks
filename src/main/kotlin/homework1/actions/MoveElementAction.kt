package homework1.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This action moves an element within a list.
 */
@Serializable
@SerialName("moveElement")
class MoveElementAction<T>(private val indexFrom: Int, private val indexTo: Int) : Action<T> {
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
