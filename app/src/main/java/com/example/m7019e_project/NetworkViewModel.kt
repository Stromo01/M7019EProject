package com.example.m7019e_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {
    private val _isNetworkConnected = MutableStateFlow(false)
    val isNetworkConnected: StateFlow<Boolean> = _isNetworkConnected

    fun setNetworkConnected(isConnected: Boolean) {
        viewModelScope.launch {
            _isNetworkConnected.emit(isConnected)
        }
    }
}