package homework3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class TestGeneratorTest {
    companion object {
        @JvmStatic
        fun yamlTestData(): List<Arguments> = listOf(
            Arguments.of(
                """
                   |package homework1
                   |
                   |import org.junit.jupiter.api.Test
                   |
                   |internal class ActionTest {
                   |  @Test
                   |  fun perform() {
                   |  }
                   |
                   |  @Test
                   |  fun undo() {
                   |  }
                   |}
                   |
                """.trimMargin(),
                TestGeneratorConfig(
                    "homework1",
                    "Action",
                    listOf("perform", "undo").map { TestGeneratorConfigFunction(it) }
                )
            ),
            Arguments.of(
                """
                   |package common.io
                   |
                   |import org.junit.jupiter.api.Test
                   |
                   |internal class InputTest {
                   |  @Test
                   |  fun read() {
                   |  }
                   |
                   |  @Test
                   |  fun parseInt() {
                   |  }
                   |
                   |  @Test
                   |  fun parseFloat() {
                   |  }
                   |}
                   |
                """.trimMargin(),
                TestGeneratorConfig(
                    "common.io",
                    "Input",
                    listOf("read", "parseInt", "parseFloat").map { TestGeneratorConfigFunction(it) }
                )
            )
        )
    }

    @MethodSource("yamlTestData")
    @ParameterizedTest(name = "test {index}, {1}")
    fun getFile(expected: String, config: TestGeneratorConfig) {
        assertEquals(expected, TestGenerator(config).file.toString())
    }
}
