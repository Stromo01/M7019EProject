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
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder

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
    val networkRequiredConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<connectWorker>(15, TimeUnit.SECONDS)
        .setConstraints(networkRequiredConstraints)
        .build()


    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "connectWorker",
        ExistingPeriodicWorkPolicy.UPDATE, // Ensures only one periodic worker runs
        workRequest
    )
}

fun scheduleDisconnectWorker(context: Context) {
    val noNetworkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<disconnectWorker>(15, TimeUnit.SECONDS)
        .setConstraints(noNetworkConstraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "disconnectWorker",
        ExistingPeriodicWorkPolicy.UPDATE, // Ensures only one periodic worker runs
        workRequest
    )
}


