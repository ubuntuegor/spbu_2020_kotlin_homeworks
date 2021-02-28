package homework_1.classes.actions

import homework_1.classes.exceptions.InvalidMoveIndexException

class MoveElementAction(val indexFrom: Int, val indexTo: Int) : Action {
    private fun moveListElement(list: MutableList<Int>, from: Int, to: Int) {
        if (from !in list.indices || to !in list.indices) throw InvalidMoveIndexException()
        val temp = list.removeAt(from)
        list.add(to, temp)
    }

    override val name: String
        get() = "Move element from index $indexFrom to $indexTo"

    override fun perform(list: MutableList<Int>) {
        moveListElement(list, indexFrom, indexTo)
    }

    override fun undo(list: MutableList<Int>) {
        moveListElement(list, indexTo, indexFrom)
    }
}
