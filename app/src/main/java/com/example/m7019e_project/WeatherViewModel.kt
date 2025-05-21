package com.example.m7019e_project

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//import com.example.m7019e.api.Movie
//import com.example.m7019e.api.MovieResponse

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherDao = AppDatabase.getDatabase(application).weatherDao()

    //var selectedMovie = mutableStateOf<Movie?>(null)
      //  private set

    //fun selectMovie(movie: Movie) {
    //    selectedMovie.value = movie
   // }

    fun cacheWeather(weatherList: List<WeatherEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherList.forEach { weather ->
                weatherDao.insertWeatherData(weather)
            }
        }
    }
    suspend fun getWeather(apiUrl: String): List<DailyWeather> {
        return try {
            val weather = fetchAndTransformWeatherData(apiUrl)

            cacheWeather(weather.mapIndexed { index, dailyWeather ->
                WeatherEntity(
                    id = index,
                    location = dailyWeather.location,
                    date = dailyWeather.date,
                    timeData = Json.encodeToString(dailyWeather.timeData)
                )
            })
            println("return: $weather")
            weather

        } catch (e: Exception) {
            println("Error fetching weather data: ${e.message}")
            getCachedWeather();
        }
    }

    suspend fun getCachedWeather(): List<DailyWeather> {
        val cachedWeather = weatherDao.getAllWeather()
        return cachedWeather.map { entity ->
            DailyWeather(
                id = entity.id,
                location = entity.location,
                date = entity.date,
                timeData = Json.decodeFromString(entity.timeData)
            )
        }
    }
    suspend fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.clearCache()
        }
    }



}