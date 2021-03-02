package homework1

import homework1.actions.Action

class PerformedCommandStorage {
    private val performedActions = mutableListOf<Action>()

    private val _storage = mutableListOf<Int>()
    val storage: List<Int>
        get() = _storage.toList()

    val lastActionName: String
        get() = performedActions.lastOrNull()?.name ?: "None"

    /**
     * @throws IllegalArgumentException if action fails with provided parameters.
     */
    fun performAction(action: Action) {
        action.perform(_storage)
        performedActions.add(action)
    }

    /**
     * @throws NoSuchElementException if no actions have been done.
     */
    fun undoLastAction() {
        val lastAction = performedActions.last()
        lastAction.undo(_storage)
        performedActions.removeLast()
    }
}
