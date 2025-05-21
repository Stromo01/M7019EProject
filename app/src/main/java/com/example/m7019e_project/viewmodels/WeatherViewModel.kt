package com.example.m7019e_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.m7019e_project.database.AppDatabase
import com.example.m7019e_project.database.WeatherEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//import com.example.m7019e.api.Movie
//import com.example.m7019e.api.MovieResponse

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherDao = AppDatabase.getDatabase(application).weatherDao()

    fun cacheWeather(weatherList: List<WeatherEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherList.forEach { weather ->
                weatherDao.insertWeatherData(weather)
            }
        }
    }
    suspend fun getWeather(apiUrl: String): List<DailyWeather> {
        var weather = fetchAndTransformWeatherData(apiUrl)
        if(weather.isEmpty()) {
            weather = getCachedWeather()
        }else{
            cacheWeather(weather.mapIndexed { index, dailyWeather ->
                WeatherEntity(
                    id = index,
                    location = dailyWeather.location,
                    date = dailyWeather.date,
                    timeData = Json.encodeToString(dailyWeather.timeData)
                )
            })

            println("Fetched and cached weather: $weather")
        }
        return weather
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