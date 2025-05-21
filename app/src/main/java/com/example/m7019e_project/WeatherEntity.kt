package com.example.m7019e_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int,
    val location: String,
    val date: String,
    val timeData: String // Store as JSON string for simplicity
)