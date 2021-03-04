package homework1.actions

class AppendToStartAction(private val value: Int) : Action {
    override val name: String
        get() = "Append $value to start"

    override fun perform(list: MutableList<Int>) {
        list.add(0, value)
    }

    override fun undo(list: MutableList<Int>) {
        list.removeFirst()
    }
}
