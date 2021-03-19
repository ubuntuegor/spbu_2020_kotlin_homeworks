package homework3

import kotlinx.serialization.Serializable

/**
 * Stores data about a function to write a test for.
 * @param name Function name.
 */
@Serializable
data class TestGeneratorConfigFunction(val name: String)
