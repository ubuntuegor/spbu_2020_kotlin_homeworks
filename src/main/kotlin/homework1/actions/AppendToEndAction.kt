package homework1.actions

/**
 * This action appends to the end of the list.
 */
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
