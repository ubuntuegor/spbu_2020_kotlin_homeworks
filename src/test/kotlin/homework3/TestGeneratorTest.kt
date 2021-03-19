package homework3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class TestGeneratorTest {
    companion object {
        @JvmStatic
        fun codeTestData(): List<Arguments> = listOf(
            Arguments.of(
                TestGeneratorTest::class.java.getResource("generatedTests/test1.kt").readText(),
                TestGeneratorConfig(
                    "homework1",
                    "Action",
                    listOf("perform", "undo").map { TestGeneratorConfigFunction(it) }
                )
            ),
            Arguments.of(
                TestGeneratorTest::class.java.getResource("generatedTests/test2.kt").readText(),
                TestGeneratorConfig(
                    "common.io",
                    "Input",
                    listOf("read", "parseInt", "parseFloat").map { TestGeneratorConfigFunction(it) }
                )
            )
        )
    }

    @MethodSource("codeTestData")
    @ParameterizedTest(name = "test {index}, {1}")
    fun getFile(expected: String, config: TestGeneratorConfig) {
        assertEquals(expected, TestGenerator(config).file.toString())
    }
}
