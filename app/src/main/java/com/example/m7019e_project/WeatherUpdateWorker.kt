package com.example.m7019e_project

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.m7019e_project.MainActivity

class WeatherUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
    navController: NavController
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        return try {
            navController.navigate("noInternetScreen")
            Result.success()
        } catch (e: Exception) {

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
        //MainActivity.reloadUI()
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