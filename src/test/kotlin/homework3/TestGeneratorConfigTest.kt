package homework3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class TestGeneratorConfigTest {
    companion object {
        @JvmStatic
        fun yamlConfigData(): List<Arguments> = listOf(
            Arguments.of(
                "test1",
                TestGeneratorConfig(
                    "homework1",
                    "Action",
                    listOf("perform", "undo").map { TestGeneratorConfigFunction(it) }
                )
            ),
            Arguments.of(
                "test2",
                TestGeneratorConfig(
                    "common.io",
                    "Input",
                    listOf("read", "parseInt", "parseFloat").map { TestGeneratorConfigFunction(it) }
                )
            )
        )
    }

    @MethodSource("yamlConfigData")
    @ParameterizedTest(name = "getConfigFromYaml - {0}")
    fun getConfigFromYaml(testName: String, expected: TestGeneratorConfig) {
        val yamlText = javaClass.getResource("yamlConfigs/$testName.yaml").readText()
        assertEquals(expected, TestGeneratorConfig.fromYaml(yamlText))
    }
}
