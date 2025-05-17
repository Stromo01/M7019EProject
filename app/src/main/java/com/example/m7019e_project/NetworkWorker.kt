package com.example.m7019e_project

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters

class NetworkWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val WORK_NAME = "NetworkWorker"
    }

    override fun doWork(): Result {
        try {
            // Notify the ViewModel about the network reconnection
            val networkViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(applicationContext as android.app.Application)
                .create(NetworkViewModel::class.java)

            networkViewModel.setNetworkConnected(true)
            Log.i("        OBS        OBS        OBS     NetworkWorker", "Network is connected, ViewModel updated.")

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}