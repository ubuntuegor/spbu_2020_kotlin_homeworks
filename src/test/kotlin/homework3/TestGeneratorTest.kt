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

    @MethodSource("codeTestData")
    @ParameterizedTest(name = "getFile - {0}")
    fun getFile(testName: String, config: TestGeneratorConfig) {
        assertEquals(
            javaClass.getResource("generatedTests/$testName.kt").readText(),
            TestGenerator(config).file.toString()
        )
    }
}
