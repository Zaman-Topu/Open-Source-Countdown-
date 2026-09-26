package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CinematicBackground
import com.example.ui.components.CountdownsSheet
import com.example.ui.components.ExpressiveCountdownDisplay
import com.example.ui.components.QuoteOfTheDaySection
import com.example.ui.components.SettingsSheet
import com.example.ui.components.SplashScreen
import com.example.ui.components.TopBarSection
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val splashStep by viewModel.splashStep.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val loadingStatus by viewModel.loadingStatus.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTimeDisplay.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDateDisplay.collectAsStateWithLifecycle()
    val countdowns by viewModel.countdowns.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedCountdownId.collectAsStateWithLifecycle()
    val isSequentialMode by viewModel.isSequentialMode.collectAsStateWithLifecycle()
    val activeTitle by viewModel.activeTitle.collectAsStateWithLifecycle()
    val activeTargetDate by viewModel.activeTargetDateDisplay.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.timeRemaining.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val currentQuote by viewModel.currentQuote.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val ambientMotionEnabled by viewModel.ambientMotionEnabled.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()

    var isCountdownsSheetOpen by remember { mutableStateOf(false) }
    var isSettingsSheetOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostStateState() }
    val scope = rememberCoroutineScope()

    Crossfade(
        targetState = splashStep >= 2,
        label = "splash_to_home_crossfade"
    ) { isHomeVisible ->
        if (!isHomeVisible) {
            SplashScreen(
                loadingProgress = loadingProgress,
                loadingStatus = loadingStatus,
                onSkip = { viewModel.dismissLoading() }
            )
        } else {
            CinematicBackground(
                themeMode = themeMode,
                isAmbientMotionEnabled = ambientMotionEnabled
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Section: Time, Date, Live Weather, and Header Actions
                        TopBarSection(
                            currentTime = currentTime,
                            currentDate = currentDate,
                            weather = weather,
                            onRefreshWeather = {
                                viewModel.refreshWeather()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Checking live weather...")
                                }
                            },
                            onOpenCountdowns = { isCountdownsSheetOpen = true },
                            onOpenSettings = { isSettingsSheetOpen = true }
                        )

                        Spacer(modifier = Modifier.weight(0.4f))

                        // Center Section: SSC 27 Large Live Countdown
                        ExpressiveCountdownDisplay(
                            title = activeTitle,
                            targetDate = activeTargetDate,
                            timeRemaining = timeRemaining,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.weight(0.6f))

                        // Bottom Section: Quote of the Day (Bangla & English)
                        QuoteOfTheDaySection(
                            quote = currentQuote,
                            onNextQuote = { viewModel.nextQuote() }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Multiple Countdowns Modal Bottom Sheet
                CountdownsSheet(
                    isOpen = isCountdownsSheetOpen,
                    countdowns = countdowns,
                    selectedId = selectedId,
                    isSequentialMode = isSequentialMode,
                    onDismiss = { isCountdownsSheetOpen = false },
                    onSelectCountdown = { id ->
                        viewModel.selectCountdown(id)
                        isCountdownsSheetOpen = false
                    },
                    onAddCountdown = { title, epochMillis, dateDisplay, timeDisplay ->
                        viewModel.addCountdown(title, epochMillis, dateDisplay, timeDisplay)
                    },
                    onEditCountdown = { item ->
                        viewModel.editCountdown(item)
                    },
                    onDeleteCountdown = { item ->
                        viewModel.deleteCountdown(item)
                    },
                    onMoveCountdown = { from, to ->
                        viewModel.moveCountdown(from, to)
                    },
                    onToggleSequentialMode = {
                        viewModel.toggleSequentialMode()
                    }
                )

                // Settings, Notifications, & AMOLED Theme Modal Bottom Sheet
                SettingsSheet(
                    isOpen = isSettingsSheetOpen,
                    themeMode = themeMode,
                    notificationsEnabled = notificationsEnabled,
                    ambientMotionEnabled = ambientMotionEnabled,
                    onDismiss = { isSettingsSheetOpen = false },
                    onSelectTheme = { mode -> viewModel.setThemeMode(mode) },
                    onToggleNotifications = { enabled ->
                        viewModel.toggleNotifications(enabled)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (enabled) "Notifications activated" else "Notifications muted"
                            )
                        }
                    },
                    onToggleAmbientMotion = { viewModel.toggleAmbientMotion() },
                    onTriggerTestNotification = {
                        viewModel.triggerTestNotification()
                        scope.launch {
                            snackbarHostState.showSnackbar("Test notification sent!")
                        }
                    }
                )
            }
        }
    }
}

private fun SnackbarHostStateState() = SnackbarHostState()
