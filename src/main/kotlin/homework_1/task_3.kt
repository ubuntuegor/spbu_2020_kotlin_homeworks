package homework_1

import homework_1.classes.actions.AppendToEndAction
import homework_1.classes.actions.AppendToStartAction
import homework_1.classes.actions.MoveElementAction
import homework_1.classes.PerformedCommandStorage
import common.exceptions.InvalidInputException
import common.promptInt
import homework_1.classes.exceptions.InvalidMoveIndexException

const val APPEND_TO_START_ACTION = 1
const val APPEND_TO_END_ACTION = 2
const val MOVE_ELEMENT_ACTION = 3
const val UNDO_ACTION = 4
const val EXIT_ACTION = 0

fun printStorage(logStorage: PerformedCommandStorage) {
    println("Current storage state: ${logStorage.storage}")
}

fun printActions(logStorage: PerformedCommandStorage) {
    println("Available actions:")
    println("  $APPEND_TO_START_ACTION. Append a value to the start of the list")
    println("  $APPEND_TO_END_ACTION. Append a value to the end of the list")
    println("  $MOVE_ELEMENT_ACTION. Move an element within the list")
    println("  $UNDO_ACTION. Undo last action (${logStorage.lastActionName})")
    println("  $EXIT_ACTION. Exit")
}

fun promptAction(): Int {
    return promptInt("Choose an action (0-4): ")
}

fun promptForAppendToStartAction(): AppendToStartAction {
    val value = promptInt("Value to append to start: ")
    return AppendToStartAction(value)
}

fun promptForAppendToEndAction(): AppendToEndAction {
    val value = promptInt("Value to append to end: ")
    return AppendToEndAction(value)
}

fun promptForMoveElementAction(): MoveElementAction {
    val indexFrom = promptInt("Index to move from: ")
    val indexTo = promptInt("Index to move to: ")
    return MoveElementAction(indexFrom, indexTo)
}

fun doAction(logStorage: PerformedCommandStorage, choice: Int): Boolean {
    val action = when (choice) {
        APPEND_TO_START_ACTION -> promptForAppendToStartAction()
        APPEND_TO_END_ACTION -> promptForAppendToEndAction()
        MOVE_ELEMENT_ACTION -> promptForMoveElementAction()
        else -> {
            println("Invalid action")
            return true
        }
    }

    try {
        logStorage.performAction(action)
    } catch (e: InvalidMoveIndexException) {
        println("Provided index is invalid")
    }

    return true
}

fun undoLastAction(logStorage: PerformedCommandStorage): Boolean {
    try {
        logStorage.undoLastAction()
    } catch (e: NoSuchElementException) {
        println("There are no actions to undo")
    }

    return true
}

fun programLoop(logStorage: PerformedCommandStorage): Boolean {
    println()
    printStorage(logStorage)
    println()
    printActions(logStorage)
    println()

    return try {
        when (val choice = promptAction()) {
            EXIT_ACTION -> false
            UNDO_ACTION -> undoLastAction(logStorage)
            else -> doAction(logStorage, choice)
        }
    } catch (e: InvalidInputException) {
        println("Invalid input")
        false
    }
}

fun main() {
    val logStorage = PerformedCommandStorage()
    do {
        val shouldContinue = programLoop(logStorage)
    } while (shouldContinue)
}
