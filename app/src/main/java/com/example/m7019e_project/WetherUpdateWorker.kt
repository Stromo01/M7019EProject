package com.example.m7019e_project

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.m7019e_project.fetchAndTransformWeatherData

class WeatherUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=65.5841&longitude=22.1547&hourly=temperature_2m"
            Log.d("WeatherUpdateWorker", "Fetching weather data from $apiUrl")
            val weatherData = fetchWeatherData(apiUrl)
            val currentTime = System.currentTimeMillis() // Get the current timestamp
            val formattedTime = java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(currentTime)) // Format the timestamp
            Log.d("WeatherUpdateWorker", "Weather data fetched successfully: $weatherData")
            saveWeatherDataToPreferences(weatherData.toString(), formattedTime) // Pass the formatted time
            Log.d("WeatherUpdateWorker", "Weather data saved to SharedPreferences")
            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherUpdateWorker", "Error in doWork", e)
            Result.retry()
        }
    }

    private suspend fun fetchWeatherData(apiUrl: String): String {
        return withContext(Dispatchers.IO) {
            val weatherData = fetchAndTransformWeatherData(apiUrl) // Fetch weather data
            val currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) // Format time as hh:mm:ss
            saveWeatherDataToPreferences(weatherData.toString(), currentTime) // Save data and formatted time
            weatherData.toString()
        }
    }

    private fun saveWeatherDataToPreferences(data: String, timestamp: String) {
        val sharedPreferences = applicationContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("latestWeatherData", data)
            .putString("dataFetchTime", timestamp) // Save the formatted time
            .apply()
    }
}

// Schedule the worker
fun scheduleWeatherUpdates(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(1, TimeUnit.MINUTES)
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}