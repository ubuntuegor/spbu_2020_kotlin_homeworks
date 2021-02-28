package homework_1.classes

class AppendToEndAction(val value: Int) : Action {
    override val name: String
        get() = "Append $value to end"

    override fun perform(list: MutableList<Int>) {
        list.add(value)
    }

    override fun undo(list: MutableList<Int>) {
        list.removeLast()
    }
}
