package homework5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class HashCodeTest {
    private val hashWrapper = HashCode

    @Test
    fun hashOf() {
        assertEquals("test".hashCode(), hashWrapper.hashOf("test", Int.MAX_VALUE))
    }
}
