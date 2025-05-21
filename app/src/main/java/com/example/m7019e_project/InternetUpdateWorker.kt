package com.example.m7019e_project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import androidx.compose.runtime.mutableStateOf
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder

object ScreenReloadState {
    val shouldReload = mutableStateOf(false)
    val isDisconnected = mutableStateOf(false)
}

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("NetworkChangeReceiver", "Network state changed")
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        if (networkCapabilities != null &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ) {
            // Network is connected
            Log.d("NetworkChangeReceiver", "Network is connected")
            scheduleConnectWorker(context)
        } else {
            // Network is disconnected
            Log.d("NetworkChangeReceiver", "Network is disconnected")
            scheduleDisconnectWorker(context)
        }
    }
}

class disconnectWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("disconnectWorker", "Network is disconnected, saving state")
        ScreenReloadState.isDisconnected.value = true
        ScreenReloadState.shouldReload.value = false
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
        ScreenReloadState.isDisconnected.value = false
        return Result.success()
    }
}

fun scheduleConnectWorker(context: Context) {
    val networkRequiredConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<connectWorker>()
        .setConstraints(networkRequiredConstraints)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "connectWorker",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

fun scheduleDisconnectWorker(context: Context) {
    val noNetworkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<disconnectWorker>()
        .setConstraints(noNetworkConstraints)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "disconnectWorker",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}


