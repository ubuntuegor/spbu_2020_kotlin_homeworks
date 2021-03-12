package homework1.actions

/**
 * This action moves an element within a list.
 */
class MoveElementAction(private val indexFrom: Int, private val indexTo: Int) : Action {
    private fun MutableList<Int>.moveElement(from: Int, to: Int) {
        if (from !in this.indices) throw IllegalArgumentException("Can't move: element $from doesn't exist")
        if (to !in this.indices) throw IllegalArgumentException("Can't move: position $to is not available")
        val temp = this.removeAt(from)
        this.add(to, temp)
    }

    override val name: String
        get() = "Move element from index $indexFrom to $indexTo"

    override fun perform(list: MutableList<Int>) {
        list.moveElement(indexFrom, indexTo)
    }

    override fun undo(list: MutableList<Int>) {
        list.moveElement(indexTo, indexFrom)
    }
}
