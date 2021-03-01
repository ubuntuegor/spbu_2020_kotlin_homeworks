package homework1.classes.actions

interface Action {
    val name: String
    fun perform(list: MutableList<Int>)
    fun undo(list: MutableList<Int>)
}
