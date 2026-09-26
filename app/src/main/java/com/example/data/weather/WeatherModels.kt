package com.example.data.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "current") val current: CurrentWeatherUnits? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherUnits(
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val relativeHumidity: Int? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "is_day") val isDay: Int? = null
)

data class WeatherInfo(
    val temperatureCelsius: Int,
    val condition: String,
    val location: String = "Dhaka",
    val isCached: Boolean = false,
    val isAvailable: Boolean = true,
    val isDay: Boolean = true
) {
    companion object {
        val Unavailable = WeatherInfo(
            temperatureCelsius = 26,
            condition = "Clear",
            location = "Dhaka",
            isCached = true,
            isAvailable = false
        )
    }
}
