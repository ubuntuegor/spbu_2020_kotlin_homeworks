package homework3

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

/**
 * Class used for storing [TestGenerator]'s input data.
 * @param packageName Package name to create tests in.
 * @param className Class to create tests for.
 * @param functions List of [TestGeneratorConfigFunction]s to test.
 */
@Serializable
data class TestGeneratorConfig(
    @SerialName("package-name")
    val packageName: String,
    @SerialName("class-name")
    val className: String,
    val functions: List<TestGeneratorConfigFunction>
) {
    companion object {
        /**
         * Get [TestGeneratorConfig] from YAML data.
         * @param yamlText Text data in YAML format.
         * @return Config object.
         */
        fun fromYaml(yamlText: String) = Yaml.default.decodeFromString<TestGeneratorConfig>(yamlText)
    }
}
