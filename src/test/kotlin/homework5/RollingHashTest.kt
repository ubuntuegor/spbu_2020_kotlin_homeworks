package homework5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class RollingHashTest {
    private val hashWrapper = RollingHash()

    @Test
    fun hashOf() {
        assertEquals(1678, hashWrapper.hashOf("test", Int.MAX_VALUE))
    }
}
