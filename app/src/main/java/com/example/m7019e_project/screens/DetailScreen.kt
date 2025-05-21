package com.example.m7019e_project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel
import androidx.compose.material3.Button

@Composable
fun DetailScreen(
    navController: NavController,
    detailScreenViewmodel: DetailScreenViewmodel,

) {
    val selectedDay = detailScreenViewmodel.selectedDay
    val selectedLocation = detailScreenViewmodel.selectedLocation
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF153f69)).padding(8.dp).padding(bottom=48.dp).verticalScroll(rememberScrollState())) {
        selectedDay.value?.let { day ->
            Banner((selectedLocation.value ?: "") , day.date, detailScreenViewmodel, navController)
            DayItem(weatherData = day, detail = true)
        }
    }

}



@Composable
fun Banner(
    location:String,
    date:String,
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
            Column(){
                Text(
                    text = location,
                    fontSize = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if(date!=""){
                    Text(
                        text = date,
                        fontSize = 28.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if(date!=""){
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
}


@Preview
@Composable
fun PreviewWeatherCard() {
    WeatherCard(
        timeData = TimestampWeather(
            time = "12:00 PM",
            temperature = 25.0f,
            windSpeed = 7.0f,
            humidity = 55.0f,
            pressure = 1012.0f,
            description = "Partly Cloudy"
        )
    )
}

