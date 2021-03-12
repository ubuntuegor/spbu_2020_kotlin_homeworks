package homework1

import homework1.actions.AppendToEndAction
import homework1.actions.AppendToStartAction
import homework1.actions.MoveElementAction
import common.io.promptInt
import java.io.FileNotFoundException

fun main() {
    Task3.run()
}

object Task3 {
    private const val APPEND_TO_START_ACTION = 1
    private const val APPEND_TO_END_ACTION = 2
    private const val MOVE_ELEMENT_ACTION = 3
    private const val UNDO_ACTION = 4
    private const val JSON_LOAD = 5
    private const val JSON_SAVE = 6
    private const val EXIT_ACTION = 0

    private const val JSON_STORAGE = "savedActions.json"

    private val logStorage = PerformedCommandStorage()

    private val userActions: String
        get() = """Available actions:
  $APPEND_TO_START_ACTION. Append a value to the start of the list
  $APPEND_TO_END_ACTION. Append a value to the end of the list
  $MOVE_ELEMENT_ACTION. Move an element within the list
  $UNDO_ACTION. Undo last action (${logStorage.lastActionName})
  $JSON_LOAD. Load actions from saved JSON.
  $JSON_SAVE. Save actions to JSON.
  $EXIT_ACTION. Exit"""

    private fun promptAction() = promptInt("Choose an action (0-6): ")

    private fun promptForAppendToStartAction() = AppendToStartAction(promptInt("Value to append to start: "))

    private fun promptForAppendToEndAction() = AppendToEndAction(promptInt("Value to append to end: "))

    private fun promptForMoveElementAction() =
        MoveElementAction(promptInt("Index to move from: "), promptInt("Index to move to: "))

    private fun PerformedCommandStorage.performActionByNumber(choice: Int) {
        val action = when (choice) {
            APPEND_TO_START_ACTION -> promptForAppendToStartAction()
            APPEND_TO_END_ACTION -> promptForAppendToEndAction()
            MOVE_ELEMENT_ACTION -> promptForMoveElementAction()
            else -> throw IllegalArgumentException("No action for $choice")
        }

        this.performAction(action)
    }

    private fun runMainLoop(): Boolean {
        println()
        println("Current storage state: ${logStorage.storage}")
        println()
        println(userActions)
        println()

        try {
            when (val choice = promptAction()) {
                EXIT_ACTION -> return false
                UNDO_ACTION -> logStorage.undoLastAction()
                JSON_LOAD -> logStorage.loadFromJson(JSON_STORAGE)
                JSON_SAVE -> logStorage.saveToJson(JSON_STORAGE)
                else -> logStorage.performActionByNumber(choice)
            }
        } catch (e: NoSuchElementException) {
            println("ERROR: No actions to undo")
        } catch (e: FileNotFoundException) {
            println("ERROR: JSON storage not found")
        } catch (e: NumberFormatException) {
            println("ERROR: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("ERROR: ${e.message}")
        }

        return true
    }

    fun run() {
        do {
            val shouldContinue = runMainLoop()
        } while (shouldContinue)
    }
}
