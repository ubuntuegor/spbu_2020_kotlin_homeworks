package homework1.actions

class AppendToEndAction(private val value: Int) : Action {
    override val name: String
        get() = "Append $value to end"

    override fun perform(list: MutableList<Int>) {
        list.add(value)
    }

    override fun undo(list: MutableList<Int>) {
        list.removeLast()
    }
}
