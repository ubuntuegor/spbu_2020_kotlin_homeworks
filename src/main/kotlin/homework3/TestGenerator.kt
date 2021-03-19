package homework3

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ClassName

/**
 * Generates a test class with specified functions.
 * @param config Describes the test class.
 * @property file Final Kotlin file.
 */
class TestGenerator(private val config: TestGeneratorConfig) {
    private val name = "${config.className}Test"
    private val testAnnotation = ClassName("org.junit.jupiter.api", "Test")

    private fun createTestFunction(function: TestGeneratorConfigFunction) = FunSpec.builder(function.name)
        .addAnnotation(testAnnotation)
        .build()

    private fun createTestClass(functions: List<TestGeneratorConfigFunction>) =
        TypeSpec.classBuilder(name)
            .addModifiers(KModifier.INTERNAL)
            .addFunctions(functions.map { createTestFunction(it) })
            .build()

    val file: FileSpec
        get() = FileSpec.builder(config.packageName, name)
            .addType(createTestClass(config.functions))
            .build()
}
