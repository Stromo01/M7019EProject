package com.example.m7019e_project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreen(
    navController: NavHostController,
    weatherData: List<DailyWeather>,
    detailScreenViewmodel: DetailScreenViewmodel,
    networkViewModel: NetworkViewModel
) {
    val isNetworkConnected by networkViewModel.isNetworkConnected.collectAsState()
    var refreshedWeatherData by remember { mutableStateOf(weatherData) }

    LaunchedEffect(isNetworkConnected) {
        if (isNetworkConnected) {
            refreshedWeatherData = runBlocking {
                val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=65.5841&longitude=22.1547&hourly=temperature_2m,wind_speed_10m,cloud_cover"
                fetchAndTransformWeatherData(apiUrl)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF153f69))
            .padding(8.dp)
            .padding(bottom = 48.dp)
    ) {
        Banner("Luleå", detailScreenViewmodel, navController)
        DisplayWeather(refreshedWeatherData, navController, detailScreenViewmodel)
    }
}


@Composable
fun Banner(
    title:String,
    detailScreenViewmodel: DetailScreenViewmodel,
    nav: NavController
) {
    Column(){
        Row( // Banner div design
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp, start = 20.dp, end = 24.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {
                    nav.navigate("video")
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Webcams")
            }
        }
    }
}


@Composable
fun DisplayWeather(weatherData:List<DailyWeather>, navController: NavController,
                   detailScreenViewmodel: DetailScreenViewmodel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(weatherData) { weather ->
            DayItem(weather,navController, detailScreenViewmodel)
        }
    }
}




@Composable
fun DayItem(
    weatherData: DailyWeather,
    navController: NavController? = null,
    detailScreenViewmodel: DetailScreenViewmodel? = null,
    detail: Boolean = false
) {
    Column(
        modifier = Modifier
            .then(
                if (!detail) {
                    Modifier.clickable {
                        detailScreenViewmodel?.selectedDay?.value = weatherData
                        navController?.navigate("weather_detail")
                    }
                } else {
                    Modifier
                }
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = weatherData.date,
            fontSize = 24.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(start = 8.dp)
                .background(Color(0xFF1a4e82), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)) // Clip the content to the rounded shape
                .background(Color(0xFF1a4e82)) // Apply the background color
        ) {
            for (timeData in weatherData.timeData.filter {
                detail || it.time in listOf("08:00", "12:00", "18:00")
            }) {
                WeatherCard(timeData)
                if (timeData != weatherData.timeData.last()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(Color(0xFF153f69))
                    )
                }
            }
        }
    }
}


@Composable
fun WeatherCard(timeData: TimestampWeather) {
    Row(
        modifier = Modifier.
        fillMaxWidth().
        background(Color(0xFF1a4e82)).
        padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = timeData.time ,
            fontSize = 20.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,

            )
        Text(
            text = timeData.temperature.toString() + "°C",
            fontSize = 20.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 20.dp)
        )
        Text(
            text = timeData.windSpeed.toString() + "m/s",
            fontSize = 20.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 20.dp)
        )
        Text(
            text = timeData.description,
            fontSize = 14.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}