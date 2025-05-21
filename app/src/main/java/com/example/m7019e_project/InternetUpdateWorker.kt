package com.example.m7019e_project

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WeatherUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val constraintType = inputData.getString("constraintType") ?: "UNKNOWN"
        if (constraintType == "CONNECTED") {
            // Notify the app to reload the current screen

        } else if (constraintType == "NOT_REQUIRED") {
            val sharedPreferences = applicationContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("lastConstraintType", constraintType)
                .apply()
        } else {
            return Result.failure()
        }

        return Result.success()
    }
}


// Schedule the worker
fun scheduleWeatherUpdates(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(1, TimeUnit.MINUTES)
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}