package homework3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)
