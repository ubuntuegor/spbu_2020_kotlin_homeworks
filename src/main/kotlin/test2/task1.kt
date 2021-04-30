package test2

import common.error.exitWithError
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt
import java.io.File
import java.io.FileNotFoundException

val API_KEY = System.getenv("OPENWEATHER_API_KEY") ?: ""

@Serializable
data class Weather(
    val weather: List<WeatherStatus>,
    val main: MainData,
    val name: String
) {
    @Serializable
    data class WeatherStatus(val id: Int, val main: String, val description: String, val icon: String)

    @Serializable
    data class MainData(val temp: Double)
}

class WeatherClient {
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getWeatherForCity(city: String) =
        client.get<Weather>("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("appid", API_KEY)
            parameter("units", "metric")
        }
}

suspend fun main() = coroutineScope {
    val cities = try {
        File("listOfCities.txt").readLines()
            .filter { it.isNotBlank() }
            .map { it.trim() }
    } catch (e: FileNotFoundException) {
        exitWithError("Please specify a list of cities in listOfCities.txt")
    }

    val weatherClient = WeatherClient()

    val requests = cities.map {
        async(Dispatchers.Default) {
            try {
                weatherClient.getWeatherForCity(it)
            } catch (e: ClientRequestException) {
                println("Error for city $it: ${e.message}")
                null
            }
        }
    }

    requests.awaitAll().filterNotNull().forEach {
        val city = it.name
        val temperature = it.main.temp.roundToInt()
        val description = it.weather.firstOrNull()?.main
        println("$city: $temperature°C, $description")
    }
}
