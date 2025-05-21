package com.example.m7019e_project

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

data class DailyWeather(
    val id: Int,
    val location: String,
    val date: String,
    val timeData: List<TimestampWeather>
)
@Serializable
data class TimestampWeather(
    val time: String,
    val temperature: Float,
    val windSpeed: Float,
    val humidity: Float,
    val pressure: Float,
    val description: String
)

@Serializable
data class WeatherApiResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyData
)

@Serializable
data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val wind_speed_10m: List<Float>,
    val cloud_cover: List<Int>
)



suspend fun fetchAndTransformWeatherData(apiUrl: String): List<DailyWeather> {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    return withContext(Dispatchers.IO) {
        try {
            val response: WeatherApiResponse = client.get(apiUrl).body()
            val groupedData = response.hourly.time.indices.groupBy { index ->
                response.hourly.time[index].substring(0, 10)
            }

            groupedData.map { (date, indices) ->
                DailyWeather(
                    id = date.hashCode(),
                    location = "${response.latitude}, ${response.longitude}",
                    date = date,
                    timeData = indices.map { index ->
                        TimestampWeather(
                            time = response.hourly.time[index].substring(11),
                            temperature = response.hourly.temperature_2m[index],
                            windSpeed = response.hourly.wind_speed_10m[index],
                            humidity = 0f,
                            pressure = 0f,
                            description = "Cloud Cover: ${response.hourly.cloud_cover[index]}%"
                        )
                    }
                )
            }
        } catch (e: UnresolvedAddressException) {
            println("Network error: ${e.message}")
            emptyList()
        } catch (e: IOException) {
            println("Network error: ${e.message}")
            emptyList()
        }
    }
}

