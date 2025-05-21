package com.example.m7019e_project

import android.annotation.SuppressLint
import android.media.tv.TvContract.Channels.Logo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel

import com.example.m7019e_project.ui.theme.M7019EProjectTheme
import kotlinx.coroutines.runBlocking
import androidx.work.Constraints

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Worker with no network required
        val noNetworkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val noNetworkWorkRequest = OneTimeWorkRequestBuilder<WeatherUpdateWorker>()
            .setConstraints(noNetworkConstraints)
            .build()

        WorkManager.getInstance(this).enqueue(noNetworkWorkRequest)

        // Worker with network required
        val networkRequiredConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val networkRequiredWorkRequest = OneTimeWorkRequestBuilder<WeatherUpdateWorker>()
            .setConstraints(networkRequiredConstraints)
            .build()

        WorkManager.getInstance(this).enqueue(networkRequiredWorkRequest)
        scheduleWeatherUpdates(this)
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun setupUI() {
        enableEdgeToEdge()
        setContent {
            val weatherViewModel: WeatherViewModel = viewModel()
            val navController = rememberNavController()
            val weatherData = runBlocking {
                val apiUrl =
                    "https://api.open-meteo.com/v1/forecast?latitude=65.5841&longitude=22.1547&hourly=temperature_2m,wind_speed_10m,cloud_cover"
                fetchAndTransformWeatherData(apiUrl)
            }
            val detailScreenViewmodel = DetailScreenViewmodel()
            M7019EProjectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                MainScreen(
                                    navController,
                                    detailScreenViewmodel
                                )
                            }
                            composable("weather_detail") {
                                DetailScreen(navController, detailScreenViewmodel)
                            }
                            composable("video") {
                                VideoScreen(navController, detailScreenViewmodel)
                            }
                            composable("noInternetScreen") {
                                NoInternetScreen(
                                    viewModel = weatherViewModel,
                                    navController = navController,
                                    detailScreenViewmodel = detailScreenViewmodel
                                )
                            }
                        }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    M7019EProjectTheme {
        Greeting("Android")
    }
}