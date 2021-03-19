package homework3

import com.charleskorn.kaml.Yaml
import common.error.exitWithError
import kotlinx.serialization.decodeFromString
import java.io.File

/**
 * Get [TestGeneratorConfig] from YAML data.
 * @param yamlText Text data in YAML format.
 * @return Config object.
 */
fun getConfigFromYaml(yamlText: String) = Yaml.default.decodeFromString<TestGeneratorConfig>(yamlText)

fun main(args: Array<String>) {
    if (args.size != 2) {
        exitWithError("Please specify yaml config as the first parameter and output directory as the second.")
    }

    val yamlFilename = args[0]
    val outputDir = args[1]

    val yamlText = File(yamlFilename).readText()
    val config = getConfigFromYaml(yamlText)
    val file = TestGenerator(config).file
    file.writeTo(File(outputDir))
}
