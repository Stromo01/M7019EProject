package com.example.m7019e_project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel
import com.example.m7019e_project.ui.theme.M7019EProjectTheme
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val weatherData = runBlocking {
                val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=65.5841&longitude=22.1547&hourly=temperature_2m,wind_speed_10m,cloud_cover"
                fetchAndTransformWeatherData(apiUrl)
            }
            val detailScreenViewmodel = DetailScreenViewmodel()
            val networkViewModel: NetworkViewModel = viewModel() // Create NetworkViewModel instance

            M7019EProjectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController, weatherData, detailScreenViewmodel, networkViewModel)
                        }
                        composable("weather_detail") {
                            DetailScreen(navController, detailScreenViewmodel, networkViewModel)
                        }
                        composable("video") {
                            VideoScreen(navController, detailScreenViewmodel, networkViewModel)
                        }
                    }
                }
            }
        }
    }
}