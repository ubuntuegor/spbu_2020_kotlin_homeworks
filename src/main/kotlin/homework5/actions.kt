package homework5

import common.io.askFor
import common.io.promptInt
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

interface UserMapAction {
    val name: String
    fun perform(map: HashMap<String, String>)
}

private fun askForKey() = askFor("key")
private fun askForValue() = askFor("value")

object PutAction : UserMapAction {
    override val name = "Put value"
    override fun perform(map: HashMap<String, String>) {
        val key = askForKey()
        val value = askForValue()
        map[key] = value
        println("Value set.")
    }
}

object GetAction : UserMapAction {
    override val name = "Get value"
    override fun perform(map: HashMap<String, String>) {
        val key = askForKey()
        val value = map[key]
        if (value == null) println("No such key in the map.")
        else println("Value: $value")
    }
}

object RemoveAction : UserMapAction {
    override val name = "Remove value"
    override fun perform(map: HashMap<String, String>) {
        val key = askForKey()
        map.remove(key)
        println("Removed.")
    }
}

object ShowStatisticsAction : UserMapAction {
    override val name = "Show statistics"
    override fun perform(map: HashMap<String, String>) {
        map.getStatistics().forEach { (key, value) ->
            println("$key: $value")
        }
    }
}

object ImportFromFileAction : UserMapAction {
    override val name = "Import from file"
    private fun askForFilename() = askFor("filename")
    private fun readFile(filename: String) = File(filename).readText()
    private fun readMapFromJson(jsonText: String) = Json.decodeFromString<Map<String, String>>(jsonText)

    override fun perform(map: HashMap<String, String>) {
        try {
            val filename = askForFilename()
            val jsonText = readFile(filename)
            val source = readMapFromJson(jsonText)
            source.forEach { (key, value) -> map[key] = value }
        } catch (e: FileNotFoundException) {
            println(e.message)
        } catch (e: SerializationException) {
            println(e.message)
        }
    }
}

object ChangeHashFunctionAction : UserMapAction {
    override val name = "Change hash function"
    private val availableHashFunctions = listOf(HashCode, RollingHash)
    private val printableHashFunctionsList =
        availableHashFunctions.mapIndexed { i, f -> "$i. ${f::class.simpleName}." }.joinToString("\n")

    override fun perform(map: HashMap<String, String>) {
        println("Available hash functions:")
        println(printableHashFunctionsList.prependIndent("  "))
        println()
        try {
            when (val choice = promptInt("Enter number: ")) {
                in availableHashFunctions.indices -> map.hashWrapper = availableHashFunctions[choice]
                else -> throw IllegalArgumentException("No such hash function.")
            }
        } catch (e: NumberFormatException) {
            println(e.message)
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}
