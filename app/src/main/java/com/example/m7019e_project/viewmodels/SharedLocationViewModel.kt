package com.example.m7019e_project.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedLocationViewModel : ViewModel() {
    val selectedCity = mutableStateOf("Luleå")
}