package homework1.actions

interface Action {
    val name: String
    fun perform(list: MutableList<Int>)
    fun undo(list: MutableList<Int>)
}
