package com.example.data.weather

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class WeatherRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ssc27_weather_cache", Context.MODE_PRIVATE)

    private val weatherService: WeatherService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherService::class.java)
    }

    suspend fun fetchWeather(): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val response = weatherService.getWeather()
            val current = response.current
            if (current?.temperature != null) {
                val temp = current.temperature.roundToInt()
                val code = current.weatherCode ?: 0
                val isDay = current.isDay != 0
                val condition = mapWmoCode(code, isDay)

                // Cache successful weather
                prefs.edit()
                    .putInt("cached_temp", temp)
                    .putString("cached_condition", condition)
                    .putBoolean("cached_is_day", isDay)
                    .putLong("cached_time", System.currentTimeMillis())
                    .apply()

                WeatherInfo(
                    temperatureCelsius = temp,
                    condition = condition,
                    location = "Kushtia",
                    isCached = false,
                    isAvailable = true,
                    isDay = isDay
                )
            } else {
                getCachedWeather()
            }
        } catch (e: Exception) {
            // Network failure must never impact the countdown
            getCachedWeather()
        }
    }

    fun getCachedWeather(): WeatherInfo {
        val temp = prefs.getInt("cached_temp", 28)
        val condition = prefs.getString("cached_condition", "Partly Cloudy") ?: "Partly Cloudy"
        val isDay = prefs.getBoolean("cached_is_day", true)
        val hasCache = prefs.contains("cached_temp")

        return WeatherInfo(
            temperatureCelsius = temp,
            condition = condition,
            location = "Kushtia",
            isCached = true,
            isAvailable = hasCache,
            isDay = isDay
        )
    }

    private fun mapWmoCode(code: Int, isDay: Boolean): String {
        return when (code) {
            0 -> if (isDay) "Sunny" else "Clear"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            71, 73, 75 -> "Snow"
            80, 81, 82 -> "Rain Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Mild"
        }
    }
}
