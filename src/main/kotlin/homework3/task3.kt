package homework3

import common.error.exitWithError
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        exitWithError("Please specify yaml config as the first parameter and output directory as the second.")
    }

    val yamlFilename = args[0]
    val outputDir = args[1]

    val yamlText = File(yamlFilename).readText()
    val config = TestGeneratorConfig.fromYaml(yamlText)
    val file = TestGenerator(config).file
    file.writeTo(File(outputDir))
}
