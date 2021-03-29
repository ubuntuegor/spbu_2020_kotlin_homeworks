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
                "test1", "abc/def/UserClassTest.kt"
            ),
            Arguments.of(
                "test2", "ru/spbu/homeworks/CheckHomeworkTest.kt"
            ),
            Arguments.of(
                "test3", "never/gonna/GiveYouUpTest.kt"
            ),
        )
    }

    @MethodSource("validationData")
    @ParameterizedTest(name = "generateTestFile - {0}")
    fun generateTestFile(testName: String, expectedPath: String, @TempDir tempDir: Path) {
        val yamlPath = javaClass.getResource("validation/$testName/config.yaml").path
        val expectedCode = javaClass.getResource("validation/$testName/expected.kt").readText()
        generateTestFile(yamlPath, tempDir.toString())
        assertEquals(expectedCode, tempDir.resolve(expectedPath).toFile().readText())
    }
}
