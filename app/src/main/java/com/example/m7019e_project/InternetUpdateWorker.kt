package com.example.m7019e_project

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.mutableStateOf
import androidx.work.ExistingPeriodicWorkPolicy

object ScreenReloadState {
    val shouldReload = mutableStateOf(false)
}


class disconnectWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("disconnectWorker", "Network is disconnected, saving state")
        val sharedPreferences = applicationContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .apply()
        return Result.success()
    }
}

class connectWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("connectWorker", "Network is connected, reloading screen")
        ScreenReloadState.shouldReload.value = true
        return Result.success()
    }
}

fun scheduleConnectWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<connectWorker>(15, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "connectWorker",
        ExistingPeriodicWorkPolicy.UPDATE, // Ensures only one periodic worker runs
        workRequest
    )
}

fun scheduleDisconnectWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<disconnectWorker>(15, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "disconnectWorker",
        ExistingPeriodicWorkPolicy.UPDATE, // Ensures only one periodic worker runs
        workRequest
    )
}


