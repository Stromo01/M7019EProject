package com.example.m7019e_project.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.m7019e_project.DailyWeather

class DetailScreenViewmodel: ViewModel()   {
    var selectedDay = mutableStateOf<DailyWeather?>(null)
    var showDialog = mutableStateOf(false)
}