package com.example.m7019e_project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(data: WeatherEntity)

    @Query("SELECT * FROM weather WHERE id = :id")
    suspend fun getWeatherData(id: Int): WeatherEntity?

    @Query("SELECT * FROM weather")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Query("DELETE FROM weather")
    suspend fun clearCache()


}