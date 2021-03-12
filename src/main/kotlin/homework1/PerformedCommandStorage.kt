package homework1

import homework1.actions.Action
import homework1.actions.AppendToEndAction
import homework1.actions.AppendToStartAction
import homework1.actions.MoveElementAction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File

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

    private val module = SerializersModule {
        polymorphic(Action::class) {
            subclass(AppendToStartAction::class)
            subclass(AppendToEndAction::class)
            subclass(MoveElementAction::class)
        }
    }

    private val format = Json { serializersModule = module }

    /**
     * Performs actions from a given file.
     * @param filename Path to the JSON file.
     */
    fun loadFromJson(filename: String) {
        val text = File(filename).readText()
        val actions = format.decodeFromString<MutableList<Action>>(text)
        actions.forEach { performAction(it) }
    }

    /**
     * Saves all actions into a file.
     * @param filename Path to the JSON file.
     */
    fun saveToJson(filename: String) {
        val text = format.encodeToString(performedActions)
        File(filename).writeText(text)
    }
}
