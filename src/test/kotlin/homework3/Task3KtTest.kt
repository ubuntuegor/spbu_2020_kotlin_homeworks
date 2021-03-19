package homework3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class Task3KtTest {
    companion object {
        @JvmStatic
        fun yamlConfigData(): List<Arguments> = listOf(
            Arguments.of(
                TestGeneratorConfig(
                    "homework1",
                    "Action",
                    listOf("perform", "undo").map { TestGeneratorConfigFunction(it) }
                ),
                """
                   |package-name: homework1
                   |class-name: Action
                   |functions:
                   |  - name: perform
                   |  - name: undo
                """.trimMargin()
            ),
            Arguments.of(
                TestGeneratorConfig(
                    "common.io",
                    "Input",
                    listOf("read", "parseInt", "parseFloat").map { TestGeneratorConfigFunction(it) }
                ),
                """
                   |package-name: common.io
                   |class-name: Input
                   |functions:
                   |  - name: "read"
                   |  - name: "parseInt"
                   |  - name: "parseFloat"
                """.trimMargin()
            )
        )
    }

    @MethodSource("yamlConfigData")
    @ParameterizedTest(name = "test {index}, {1}")
    fun getConfigFromYaml(expected: TestGeneratorConfig, yamlText: String) {
        assertEquals(expected, getConfigFromYaml(yamlText))
    }
}
