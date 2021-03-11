package homework1.actions

/**
 * Describes an action which can be applied to a list of integers.
 */
interface Action {
    /**
     * A descriptive name of this action.
     */
    val name: String

    /**
     * Applies the action.
     * @param list list which the action will be applied to.
     */
    fun perform(list: MutableList<Int>)

    /**
     * Reverts the changes done by [perform], assuming that the action was the last one applied to the list.
     * @param list list to revert the action in.
     */
    fun undo(list: MutableList<Int>)
}
