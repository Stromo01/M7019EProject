package com.example.m7019e_project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m7019e_project.DailyWeather
import com.example.m7019e_project.MainScreen
import com.example.m7019e_project.R
import com.example.m7019e_project.WeatherViewModel
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel

@Composable
fun NoInternetScreen(
    viewModel: WeatherViewModel,
    navController: NavController,
    detailScreenViewmodel: DetailScreenViewmodel
) {
    var cachedWeather by remember { mutableStateOf(emptyList<DailyWeather>()) }

    LaunchedEffect(Unit) {
        cachedWeather = viewModel.getCachedWeather()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cachedWeather.isNotEmpty()) {
            navController.navigate("main") {
                popUpTo("main") { inclusive = true }
            }
            MainScreen(
                navController,
                detailScreenViewmodel
            )
        } else {
            Text(
                text = "No Internet Connection",
                fontSize = 24.sp,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            Icon(
                painter = painterResource(id = R.drawable.no_wifi),
                contentDescription = "No cached movies",
                tint = Color.Gray,
                modifier = Modifier.fillMaxSize(0.3f)
            )
        }

    }
}