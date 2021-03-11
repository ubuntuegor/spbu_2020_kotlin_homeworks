package homework1

import homework1.actions.Action

/**
 * Stores an ordered list of integers along with the history of changes.
 */
class PerformedCommandStorage {
    private val performedActions = mutableListOf<Action>()

    private val _storage = mutableListOf<Int>()

    /**
     * Current state of the storage in form of a [List].
     */
    val storage: List<Int>
        get() = _storage.toList()

    /**
     * A descriptive name of the last applied action.
     */
    val lastActionName: String
        get() = performedActions.lastOrNull()?.name ?: "None"

    /**
     * Performs an action on the storage specified by a subclass of [Action].
     * If succeeded, the storage remembers this action.
     * @param action Action to be applied.
     * @throws IllegalArgumentException If action fails with provided parameters.
     */
    fun performAction(action: Action) {
        action.perform(_storage)
        performedActions.add(action)
    }

    /**
     * Reverts the last applied action.
     * @throws NoSuchElementException If no actions have been done.
     */
    fun undoLastAction() {
        val lastAction = performedActions.last()
        lastAction.undo(_storage)
        performedActions.removeLast()
    }
}
