package com.example.m7019e_project

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel
import com.example.m7019e_project.viewmodels.SharedLocationViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreen(
    navController: NavController,
    //weatherData: List<DailyWeather>,
    detailScreenViewmodel: DetailScreenViewmodel,
    sharedLocationViewModel: SharedLocationViewModel

) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
    val currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
    var textState by sharedLocationViewModel.selectedCity
    var expanded by remember { mutableStateOf(false) }
    var weatherData by remember { mutableStateOf<List<DailyWeather>>(emptyList()) }
    val locations = listOf("Luleå", "Stockholm", "Gothenburg", "Malmö")
    val weatherViewModel: WeatherViewModel = viewModel()
    val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=${getLatitude(textState)}&longitude=${getLongitude(textState)}&hourly=temperature_2m,wind_speed_10m,cloud_cover"

    runBlocking {
        weatherData =  weatherViewModel.getWeather(apiUrl)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF153f69))
            .padding(8.dp)
            .padding(bottom = 48.dp)
    ) {
        MainBanner(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            textState = textState,
            onTextStateChange = { textState = it },
            locations = locations,
            navController = navController,
            onWeatherDataChange = { weatherData = it },
            detailScreenViewmodel = detailScreenViewmodel
        )
        if (weatherData.isEmpty()) {
            Text("No weather data available", color = Color.White)
        } else {
            DisplayWeather(weatherData, navController, detailScreenViewmodel)
        }
    }
}


@Composable
fun MainBanner(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    textState: String,
    onTextStateChange: (String) -> Unit,
    locations: List<String>,
    navController: NavController
    , onWeatherDataChange: (List<DailyWeather>) -> Unit,
    detailScreenViewmodel: DetailScreenViewmodel
) {
    Row(
        modifier = Modifier
            .padding(top=45.dp),
    ){
    Box(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { onExpandedChange(true) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1a4e82),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(200.dp)


        ) {
            Text(
                text = textState,
                color = Color.White,
                fontSize = 20.sp
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location) },
                    onClick = {
                        onTextStateChange(location)
                        onExpandedChange(false)
                        detailScreenViewmodel.selectedLocation.value = location
                    }
                )

                LaunchedEffect(textState) {
                    val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=${getLatitude(textState)}&longitude=${getLongitude(textState)}&hourly=temperature_2m,wind_speed_10m,cloud_cover"
                    onWeatherDataChange(fetchAndTransformWeatherData(apiUrl))
                }
            }
        }
    }
        Button(
            onClick = {
                navController.navigate("video")
            },
            modifier = Modifier
                .padding(8.dp)
        ) {
        Text(text = "Video")
    }
    }
}


fun getLatitude(location: String): Double {
    return when (location) {
        "Luleå" -> 65.5841
        "Stockholm" -> 59.3293
        "Gothenburg" -> 57.7089
        "Malmö" -> 55.6050
        else -> 0.0
    }
}

fun getLongitude(location: String): Double {
    return when (location) {
        "Luleå" -> 22.1547
        "Stockholm" -> 18.0686
        "Gothenburg" -> 11.9746
        "Malmö" -> 13.0038
        else -> 0.0
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