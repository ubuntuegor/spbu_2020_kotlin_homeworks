package homework_1.classes

interface Action {
    val name: String
    fun perform(list: MutableList<Int>)
    fun undo(list: MutableList<Int>)
}
