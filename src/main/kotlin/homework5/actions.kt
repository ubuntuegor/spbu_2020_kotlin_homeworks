package homework5

import common.io.askFor
import common.io.promptInt
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

interface UserMapAction {
    val map: HashMap<String, String>
    fun perform()
}

private fun askForKey() = askFor("key")
private fun askForValue() = askFor("value")

class PutAction(override val map: HashMap<String, String>) : UserMapAction {
    override fun perform() {
        val key = askForKey()
        val value = askForValue()
        map[key] = value
        println("Value set.")
    }
}

class GetAction(override val map: HashMap<String, String>) : UserMapAction {
    override fun perform() {
        val key = askForKey()
        val value = map[key]
        if (value == null) println("No such key in the map.")
        else println("Value: $value")
    }
}

class RemoveAction(override val map: HashMap<String, String>) : UserMapAction {
    override fun perform() {
        val key = askForKey()
        map.remove(key)
        println("Removed.")
    }
}

class ShowStatisticsAction(override val map: HashMap<String, String>) : UserMapAction {
    override fun perform() {
        map.getStatistics().forEach { (key, value) ->
            println("$key: $value")
        }
    }
}

class ImportFromFileAction(override val map: HashMap<String, String>) : UserMapAction {
    private fun askForFilename() = askFor("filename")
    private fun readFile(filename: String) = File(filename).readText()
    private fun readMapFromJson(jsonText: String) = Json.decodeFromString<Map<String, String>>(jsonText)

    override fun perform() {
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

class ChangeHashFunctionAction(override val map: HashMap<String, String>) : UserMapAction {
    private val functions = """
            Available hash functions:
              1. Hash Code.
              2. Rolling Hash.
        """.trimIndent()

    override fun perform() {
        println(functions)
        println()
        try {
            when (promptInt("Enter number: ")) {
                1 -> map.hashWrapper = HashCode()
                2 -> map.hashWrapper = RollingHash()
                else -> throw IllegalArgumentException("No such hash function.")
            }
        } catch (e: NumberFormatException) {
            println(e.message)
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}
