package com.example.data.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double = 23.9013,
        @Query("longitude") longitude: Double = 89.1205,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,is_day",
        @Query("timezone") timezone: String = "Asia/Dhaka"
    ): OpenMeteoResponse
}
