package homework_1.classes

class PerformedCommandStorage {
    private val _performedActions = mutableListOf<Action>()
    private val _storage = mutableListOf<Int>()

    val storage: List<Int>
        get() = _storage.toList()

    val lastActionName: String
        get() = _performedActions.lastOrNull()?.name ?: "None"

    fun performAction(action: Action) {
        action.perform(_storage)
        _performedActions.add(action)
    }

    fun undoLastAction() {
        val lastAction = _performedActions.last()
        lastAction.undo(_storage)
        _performedActions.removeLast()
    }
}
