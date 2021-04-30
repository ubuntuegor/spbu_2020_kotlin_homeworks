package test2

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class WeatherClientTest {
    private val weatherClient = WeatherClient()

    @Test
    fun getWeatherForCity() {
        val weather = runBlocking { weatherClient.getWeatherForCity("Kotlas") }
        assertEquals("Kotlas", weather.name)
    }
}
