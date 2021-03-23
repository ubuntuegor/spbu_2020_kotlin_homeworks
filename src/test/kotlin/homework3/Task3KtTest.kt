package homework3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path

internal class Task3KtTest {
    companion object {
        @JvmStatic
        fun validationData(): List<Arguments> = listOf(
            Arguments.of(
                "homework1/ActionTest.kt",
                TestGeneratorConfigTest::class.java.getResource("generatedTests/test1.kt").readText(),
                TestGeneratorConfigTest::class.java.getResource("yamlConfigs/test1.yaml").path
            ),
            Arguments.of(
                "common/io/InputTest.kt",
                TestGeneratorConfigTest::class.java.getResource("generatedTests/test2.kt").readText(),
                TestGeneratorConfigTest::class.java.getResource("yamlConfigs/test2.yaml").path
            ),
            Arguments.of(
                "abc/def/UserClassTest.kt",
                TestGeneratorConfigTest::class.java.getResource("validation/test1/expected.kt").readText(),
                TestGeneratorConfigTest::class.java.getResource("validation/test1/config.yaml").path
            ),
            Arguments.of(
                "ru/spbu/homeworks/CheckHomeworkTest.kt",
                TestGeneratorConfigTest::class.java.getResource("validation/test2/expected.kt").readText(),
                TestGeneratorConfigTest::class.java.getResource("validation/test2/config.yaml").path
            ),
            Arguments.of(
                "never/gonna/GiveYouUpTest.kt",
                TestGeneratorConfigTest::class.java.getResource("validation/test3/expected.kt").readText(),
                TestGeneratorConfigTest::class.java.getResource("validation/test3/config.yaml").path
            ),
        )
    }

    @MethodSource("validationData")
    @ParameterizedTest(name = "test {index}, {2}")
    fun generateTestFile(expectedPath: String, expectedCode: String, yamlPath: String, @TempDir tempDir: Path) {
        generateTestFile(yamlPath, tempDir.toString())
        assertEquals(expectedCode, tempDir.resolve(expectedPath).toFile().readText())
    }
}
