package com.example.m7019e_project

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Constraints
import androidx.work.NetworkType
import android.os.Handler
import android.os.Looper
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.example.m7019e_project.WeatherUpdateWorker

class NetworkConnectionHandler(
    private val context: Context,
    private val onNetworkAvailable: () -> Unit,
    private val onNetworkLost: () -> Unit
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val handler = Handler(Looper.getMainLooper())

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            handler.post {
                onNetworkAvailable()
                triggerNetworkWorker(true) // Trigger WorkManager
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            handler.post {
                onNetworkLost()
                triggerNetworkWorker(false) // Trigger WorkManager
            }
        }
    }

    private fun triggerNetworkWorker(isConnected: Boolean) {
        val inputData = Data.Builder()
            .putBoolean("isConnected", isConnected)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherUpdateWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun startListening() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}