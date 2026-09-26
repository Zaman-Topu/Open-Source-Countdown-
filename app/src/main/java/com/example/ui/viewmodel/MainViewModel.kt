package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CountdownItem
import com.example.data.quotes.MotivationalQuotes
import com.example.data.quotes.Quote
import com.example.data.repository.CountdownRepository
import com.example.data.weather.WeatherInfo
import com.example.data.weather.WeatherRepository
import com.example.notification.NotificationHelper
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CountdownTimeRemaining(
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val isTargetReached: Boolean = false
)

class MainViewModel(
    application: Application,
    private val repository: CountdownRepository,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ssc27_user_prefs", Context.MODE_PRIVATE)

    // Current live time & date
    private val _currentTimeDisplay = MutableStateFlow("")
    val currentTimeDisplay: StateFlow<String> = _currentTimeDisplay.asStateFlow()

    private val _currentDateDisplay = MutableStateFlow("")
    val currentDateDisplay: StateFlow<String> = _currentDateDisplay.asStateFlow()

    // Countdowns from Room
    val countdowns: StateFlow<List<CountdownItem>> = repository.allCountdowns
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCountdownId = MutableStateFlow<Long?>(null)
    val selectedCountdownId: StateFlow<Long?> = _selectedCountdownId.asStateFlow()

    // Sequential mode
    private val _isSequentialMode = MutableStateFlow(prefs.getBoolean("key_sequential_mode", false))
    val isSequentialMode: StateFlow<Boolean> = _isSequentialMode.asStateFlow()

    // Active Countdown Details
    private val _activeTitle = MutableStateFlow("SSC 27")
    val activeTitle: StateFlow<String> = _activeTitle.asStateFlow()

    private val _activeTargetDateDisplay = MutableStateFlow("7 January 2027")
    val activeTargetDateDisplay: StateFlow<String> = _activeTargetDateDisplay.asStateFlow()

    private val _activeTargetEpochMillis = MutableStateFlow(1799380800000L)
    val activeTargetEpochMillis: StateFlow<Long> = _activeTargetEpochMillis.asStateFlow()

    private val _timeRemaining = MutableStateFlow(CountdownTimeRemaining())
    val timeRemaining: StateFlow<CountdownTimeRemaining> = _timeRemaining.asStateFlow()

    // Weather
    private val _weather = MutableStateFlow(weatherRepository.getCachedWeather())
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    // Quotes
    private val _currentQuote = MutableStateFlow(MotivationalQuotes.getDailyQuote())
    val currentQuote: StateFlow<Quote> = _currentQuote.asStateFlow()

    // Theme Mode
    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.getString("key_theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Ambient Motion Setting
    private val _ambientMotionEnabled = MutableStateFlow(prefs.getBoolean("key_ambient_motion", true))
    val ambientMotionEnabled: StateFlow<Boolean> = _ambientMotionEnabled.asStateFlow()

    // Notifications Setting
    private val _notificationsEnabled = MutableStateFlow(NotificationHelper.isNotificationsEnabled(application))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Splash state: 0 = Splash Logo, 1 = Motivational Quote Reveal, 2 = Main Screen
    private val _splashStep = MutableStateFlow(0)
    val splashStep: StateFlow<Int> = _splashStep.asStateFlow()

    init {
        NotificationHelper.initNotificationChannels(application)
        if (NotificationHelper.isNotificationsEnabled(application)) {
            NotificationHelper.scheduleAllAlarms(application)
        }

        startSplashSequence()
        startLiveClockAndCountdown()
        refreshWeather()
    }

    private fun startSplashSequence() {
        viewModelScope.launch {
            // Step 0: SSC 27 Logo & subtle motion
            delay(1200)
            // Step 1: Unique motivational quote reveal
            _splashStep.value = 1
            delay(1100)
            // Step 2: Smooth transition into Main Home Screen
            _splashStep.value = 2
        }
    }

    private fun startLiveClockAndCountdown() {
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH)
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH)
        val dhakaZone = ZoneId.of("Asia/Dhaka")

        viewModelScope.launch {
            while (true) {
                val nowZoned = ZonedDateTime.now(dhakaZone)
                _currentTimeDisplay.value = nowZoned.format(timeFormatter)
                _currentDateDisplay.value = nowZoned.format(dateFormatter)

                calculateCountdown(nowZoned)
                delay(1000)
            }
        }
    }

    private fun calculateCountdown(nowZoned: ZonedDateTime) {
        val list = countdowns.value
        val nowEpoch = nowZoned.toInstant().toEpochMilli()

        val activeItem: CountdownItem? = if (isSequentialMode.value && list.isNotEmpty()) {
            // Sequential Mode: first countdown whose target is still in the future
            list.firstOrNull { it.targetEpochMillis > nowEpoch } ?: list.lastOrNull()
        } else {
            val selectedId = _selectedCountdownId.value
            list.firstOrNull { it.id == selectedId }
                ?: list.firstOrNull { it.isDefault }
                ?: list.firstOrNull()
        }

        if (activeItem != null) {
            _activeTitle.value = activeItem.title
            _activeTargetDateDisplay.value = activeItem.targetDateDisplay
            _activeTargetEpochMillis.value = activeItem.targetEpochMillis

            NotificationHelper.updateActiveCountdown(
                getApplication(),
                activeItem.title,
                activeItem.targetEpochMillis
            )

            val diffMillis = activeItem.targetEpochMillis - nowEpoch
            if (diffMillis <= 0) {
                _timeRemaining.value = CountdownTimeRemaining(0, 0, 0, 0, isTargetReached = true)
            } else {
                val seconds = (diffMillis / 1000) % 60
                val minutes = (diffMillis / (1000 * 60)) % 60
                val hours = (diffMillis / (1000 * 60 * 60)) % 24
                val days = diffMillis / (1000 * 60 * 60 * 24)

                _timeRemaining.value = CountdownTimeRemaining(
                    days = days,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    isTargetReached = false
                )
            }
        } else {
            // Default official SSC 2027 fallback calculation (7 January 2027 at 10:00 AM BST)
            val dhakaZone = ZoneId.of("Asia/Dhaka")
            val defaultTarget = ZonedDateTime.of(2027, 1, 7, 10, 0, 0, 0, dhakaZone).toInstant().toEpochMilli()
            val diff = defaultTarget - nowEpoch

            _activeTitle.value = "SSC 27"
            _activeTargetDateDisplay.value = "7 January 2027"
            _activeTargetEpochMillis.value = defaultTarget

            NotificationHelper.updateActiveCountdown(
                getApplication(),
                "SSC 27",
                defaultTarget
            )

            if (diff <= 0) {
                _timeRemaining.value = CountdownTimeRemaining(0, 0, 0, 0, isTargetReached = true)
            } else {
                val seconds = (diff / 1000) % 60
                val minutes = (diff / (1000 * 60)) % 60
                val hours = (diff / (1000 * 60 * 60)) % 24
                val days = diff / (1000 * 60 * 60 * 24)

                _timeRemaining.value = CountdownTimeRemaining(
                    days = days,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    isTargetReached = false
                )
            }
        }
    }

    fun selectCountdown(id: Long) {
        _selectedCountdownId.value = id
    }

    fun addCountdown(
        title: String,
        targetEpochMillis: Long,
        targetDateDisplay: String,
        targetTimeDisplay: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.getCount()
            val newItem = CountdownItem(
                title = title.trim(),
                targetEpochMillis = targetEpochMillis,
                targetDateDisplay = targetDateDisplay,
                targetTimeDisplay = targetTimeDisplay,
                orderIndex = count,
                isDefault = false,
                isCompleted = false
            )
            val newId = repository.insert(newItem)
            _selectedCountdownId.value = newId
        }
    }

    fun editCountdown(item: CountdownItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(item)
        }
    }

    fun deleteCountdown(item: CountdownItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(item)
            if (_selectedCountdownId.value == item.id) {
                _selectedCountdownId.value = null
            }
        }
    }

    fun moveCountdown(fromIndex: Int, toIndex: Int) {
        val currentList = countdowns.value.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            viewModelScope.launch(Dispatchers.IO) {
                repository.reorder(currentList)
            }
        }
    }

    fun toggleSequentialMode() {
        val updated = !_isSequentialMode.value
        _isSequentialMode.value = updated
        prefs.edit().putBoolean("key_sequential_mode", updated).apply()
    }

    fun nextQuote() {
        val currentId = _currentQuote.value.id
        _currentQuote.value = MotivationalQuotes.getRandomQuote(excludeId = currentId)
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val updated = weatherRepository.fetchWeather()
            _weather.value = updated
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("key_theme_mode", mode.name).apply()
    }

    fun toggleAmbientMotion() {
        val updated = !_ambientMotionEnabled.value
        _ambientMotionEnabled.value = updated
        prefs.edit().putBoolean("key_ambient_motion", updated).apply()
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        NotificationHelper.setNotificationsEnabled(getApplication(), enabled)
    }

    fun triggerTestNotification() {
        NotificationHelper.triggerTestNotification(getApplication())
    }

    companion object {
        fun provideFactory(
            application: Application,
            repository: CountdownRepository,
            weatherRepository: WeatherRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(application, repository, weatherRepository) as T
            }
        }
    }
}
