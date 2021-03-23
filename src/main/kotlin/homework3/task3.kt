package homework3

import common.error.exitWithError
import java.io.File

fun generateTestFile(yamlPath: String, outputDir: String) {
    val yamlConfig = File(yamlPath).readText()
    val config = TestGeneratorConfig.fromYaml(yamlConfig)
    val file = TestGenerator(config).file
    file.writeTo(File(outputDir))
}

fun main(args: Array<String>) {
    if (args.size != 2) {
        exitWithError("Please specify yaml config as the first parameter and output directory as the second.")
    }
    generateTestFile(args[0], args[1])
}
