package homework1

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File
import homework1.actions.Action
import homework1.actions.AppendToStartAction
import homework1.actions.AppendToEndAction
import homework1.actions.MoveElementAction
import homework1.serialization.IntAsObjectSerializer
import kotlinx.serialization.PolymorphicSerializer

/**
 * Stores an ordered list of T along with the history of changes.
 */
class PerformedCommandStorage<T> {
    private val performedActions = mutableListOf<Action<T>>()

    private val _storage = mutableListOf<T>()

    /**
     * Current state of the storage in form of a [List].
     */
    val storage: List<T>
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
    fun performAction(action: Action<T>) {
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

    companion object IntJson {
        private val module = SerializersModule {
            polymorphic(Any::class) {
                subclass(IntAsObjectSerializer)
            }
            polymorphic(Action::class) {
                subclass(AppendToStartAction.serializer(PolymorphicSerializer(Any::class)))
                subclass(AppendToEndAction.serializer(PolymorphicSerializer(Any::class)))
                subclass(MoveElementAction.serializer(PolymorphicSerializer(Any::class)))
            }
        }

        private val format = Json { serializersModule = module }

        /**
         * Performs actions from a given file.
         * @param filename Path to the JSON file.
         */
        fun PerformedCommandStorage<Int>.loadFromJson(filename: String) {
            val text = File(filename).readText()
            val actions = format.decodeFromString<MutableList<Action<Int>>>(text)
            actions.forEach { this.performAction(it) }
        }

        /**
         * Saves all actions into a file.
         * @param filename Path to the JSON file.
         */
        fun PerformedCommandStorage<Int>.saveToJson(filename: String) {
            val text = format.encodeToString(this.performedActions)
            File(filename).writeText(text)
        }
    }
}
