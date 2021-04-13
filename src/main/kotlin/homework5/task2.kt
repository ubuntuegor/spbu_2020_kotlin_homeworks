package homework5

import common.io.askFor

fun main() {
    println("Hash Map CLI")
    do {
        val shouldContinue = Task2.runUserAction()
    } while (shouldContinue)
}

object Task2 {
    private val hashMap = HashMap<String, String>(HashCode())

    private val userActions = """
            Available actions:
              (p)ut value.
              (r)emove value.
              (g)et value.
              (s)how statistics.
              (i)mport from file.
              (c)hange hash function.
              (e)xit.
        """.trimIndent()

    fun runUserAction(): Boolean {
        println()
        println(userActions)
        println()

        val choice = askFor("choice")
        println()

        when (choice.first()) {
            'p' -> PutAction(hashMap)
            'r' -> RemoveAction(hashMap)
            'g' -> GetAction(hashMap)
            's' -> ShowStatisticsAction(hashMap)
            'i' -> ImportFromFileAction(hashMap)
            'c' -> ChangeHashFunctionAction(hashMap)
            'e' -> return false
            else -> {
                println("No such action.")
                null
            }
        }?.perform()

        return true
    }
}
