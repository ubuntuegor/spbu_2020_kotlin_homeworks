package homework5

import common.io.askFor

fun main() {
    println("Hash Map CLI")
    do {
        val shouldContinue = Task2.runUserAction()
    } while (shouldContinue)
}

object Task2 {
    private val hashMap = HashMap<String, String>(HashCode)

    private const val exitActionTrigger = 'e'
    private const val exitActionListing = "(e)xit"
    private val availableActions =
        listOf(GetAction, PutAction, RemoveAction, ShowStatisticsAction, ImportFromFileAction, ChangeHashFunctionAction)

    private val printableActionList =
        availableActions.map { it.name.toLowerCase() }.joinToString("\n") { "(${it.first()})${it.drop(1)}." }
            .plus("\n$exitActionListing.")

    fun runUserAction(): Boolean {
        println()
        println("Available actions:")
        println(printableActionList.prependIndent("  "))
        println()

        val choice = askFor("choice").firstOrNull()

        if (choice == exitActionTrigger) return false

        println()
        availableActions.find { choice == it.name.toLowerCase().first() }?.perform(hashMap)
            ?: println("No such action.")

        return true
    }
}
