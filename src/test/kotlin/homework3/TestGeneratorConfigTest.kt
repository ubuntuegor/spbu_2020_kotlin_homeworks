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
                TestGeneratorConfig(
                    "homework1",
                    "Action",
                    listOf("perform", "undo").map { TestGeneratorConfigFunction(it) }
                ), TestGeneratorConfigTest::class.java.getResource("yamlConfigs/test1.yaml").readText()
            ),
            Arguments.of(
                TestGeneratorConfig(
                    "common.io",
                    "Input",
                    listOf("read", "parseInt", "parseFloat").map { TestGeneratorConfigFunction(it) }
                ), TestGeneratorConfigTest::class.java.getResource("yamlConfigs/test2.yaml").readText()
            )
        )
    }

    @MethodSource("yamlConfigData")
    @ParameterizedTest(name = "test {index}, {1}")
    fun getConfigFromYaml(expected: TestGeneratorConfig, yamlText: String) {
        assertEquals(expected, TestGeneratorConfig.fromYaml(yamlText))
    }
}
