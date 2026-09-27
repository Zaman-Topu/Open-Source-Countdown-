# 🎯 SSC 27 — Countdown & Focus Companion

<p align="center">
  <img src="app/src/main/res/drawable/app_logo.png" width="128" height="128" alt="SSC 27 Logo" style="border-radius: 28px;" />
</p>

<p align="center">
  <b>A sleek, privacy-first, Material 3 countdown and focus companion designed specifically for Bangladeshi SSC Batch 2027 students.</b>
</p>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-100%25-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" /></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg?style=for-the-badge&logo=android&logoColor=white" alt="Jetpack Compose" /></a>
  <a href="https://developer.android.com/training/data-storage/room"><img src="https://img.shields.io/badge/Room%20DB-Offline%20First-2E7D32.svg?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room DB" /></a>
  <a href="https://developer.android.com/about/versions/14"><img src="https://img.shields.io/badge/Android-10%20to%2015+-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white" alt="Android Support" /></a>
  <a href="download/SSC27.apk"><img src="https://img.shields.io/badge/Download%20APK-v1.5-FF6F00.svg?style=for-the-badge&logo=googleplay&logoColor=white" alt="Download APK" /></a>
</p>

---

## 📖 Overview

**SSC 27** is built to keep Bangladeshi secondary school certificate candidates laser-focused on their ultimate goal: the **SSC Examination 2027 (7 January 2027, 10:00 AM BST)**.

Unlike cluttered study apps filled with disruptive ads and trackers, **SSC 27** delivers an immediate, distraction-free Google Material 3 interface that launches straight into your live countdown—with zero splash delays, zero telemetry, and complete offline capability.

---

## ✨ Key Features

### ⏱️ Precision Live Countdown & BST Clock
- **Live Tick (1 Hz):** Millisecond-accurate countdown tracking Days, Hours, Minutes, and Seconds.
- **Bangladesh Standard Time (BST):** Built-in live Dhaka time and Bengali calendar synchronisation.
- **Milestone Progress Arc:** Visual percentage calculation of journey completion from study start to exam day.

### 📝 100% Pure-Compose Milestone Editor (Android 10+ Crash-Proof)
- **Direct 1-Tap Editing:** Tap the pencil (`✏️`) icon right on the home screen or inside the countdowns sheet.
- **Zero Native Dialog Crashes:** Built entirely in Jetpack Compose to eliminate `WindowManager.BadTokenException` errors on Android 10, 11, 12, 13, 14, and 15+.
- **Quick Milestones:**
  - 🎯 **SSC 2027 Final** (`7 January 2027`)
  - 📝 **SSC Test Examination** (`10 December 2026`)
  - 📚 **SSC Pre-Test Revision** (`15 November 2026`)
  - ⏳ **Model Test Exam** (`1 October 2026`)
- **Interactive Steppers & Chips:** Easy year, month, and day pickers with dynamic AM/PM toggles and real-time days-remaining previews.

### 🔄 Intelligent Sequential Mode
- When enabled, as soon as an earlier milestone concludes (e.g., Pre-Test), the app automatically promotes the next exam target (Test Exam, then SSC Final) to the primary countdown position without manual switching.

### 🔔 Smart & Resilient Notifications
- **High-Priority Channels:** Heads-up alerts with sound and vibration configured for Android 8.0 through 15+.
- **Valid Monochrome Small Icons:** Uses dedicated vector drawables (`ic_stat_notification.xml`) to prevent system drops on OEM skins (MIUI, HyperOS, OneUI, ColorOS).
- **Exact Alarm Fallback:** Handles Android 13+ `POST_NOTIFICATIONS` runtime permissions and Android 12+ `SCHEDULE_EXACT_ALARM` gracefully.
- **Notification Schedule:**
  - ⚡ *Every 5 Hours:* Quick focus boost & study quotes (Bangla + English).
  - 📅 *Every 12 Hours:* Active countdown reminder with exact days remaining.
  - 🌟 *Every 24 Hours:* Daily profound Bengali inspirational quotes.
- **Instant Test Trigger:** In-app button to immediately dispatch a test notification and confirm device delivery.

### 📜 Bilingual Daily Inspiration
- Handcrafted library of uplifting quotes by celebrated Bangladeshi scholars, scientists, and world philosophers (translated into authentic Bengali and English).
- Single-tap quote cycling with smooth crossfade transitions.

### ⛅ Live Dhaka Weather
- Live temperature, apparent feels-like temperature, and meteorological conditions fetched via lightweight Open-Meteo REST API.
- Non-blocking coroutines with cached fallback for offline usage.

### 🎨 Material 3 Aesthetic & OLED Dark Mode
- **System / Light / Dark / AMOLED Pure Black:** True `#000000` AMOLED theme for maximum battery saving during late-night study sessions.
- **Lag-Free Ambient Canvas:** High-efficiency static gradient orbs tailored for smooth 60/120 FPS performance even on low-end hardware.

---

## 🏗️ Architecture & Technology Stack

SSC 27 is engineered following **Modern Android Architecture (MVVM + Clean Architecture)** principles recommended by Google:

```text
┌─────────────────────────────────────────────────────────────┐
│                       UI Layer                              │
│   Jetpack Compose · Material 3 · StateFlow · State Holders   │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes UI State
┌──────────────────────────────▼──────────────────────────────┐
│                    ViewModel Layer                          │
│   MainViewModel · CoroutineScope · Live Countdown Loop      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Dispatches Actions
┌──────────────────────────────▼──────────────────────────────┐
│                    Repository Layer                         │
│     CountdownRepository       │      WeatherRepository       │
└──────────────┬────────────────┴──────────────┬──────────────┘
               │                               │
┌──────────────▼──────────────┐ ┌──────────────▼──────────────┐
│        Room Database        │ │      Retrofit & Moshi       │
│  SQLite Local Persistence   │ │   Open-Meteo Weather API    │
└─────────────────────────────┘ └─────────────────────────────┘
```

| Component | Technology | Description |
|---|---|---|
| **Language** | Kotlin 2.0+ | Modern, expressive, and type-safe |
| **UI Toolkit** | Jetpack Compose (BOM 2024.09.00) | Declarative UI with Material 3 styling |
| **Local Database** | Room 2.6.1 + KSP | Offline-first SQLite database for countdowns |
| **Asynchrony** | Coroutines & Flow | Non-blocking background operations |
| **Networking** | Retrofit 2.11 + Moshi | Lightweight REST client for Dhaka weather |
| **Notifications** | Android NotificationManager + AlarmManager | Exact alarm scheduling & background delivery |
| **Min SDK** | API 26 (Android 8.0 Oreo) | Supports >95% of active Android devices |
| **Target SDK** | API 36 (Android 16 DP) | Fully compliant with newest Google Play guidelines |

---

## 📂 Project Directory Structure

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/
│   ├── MainActivity.kt                      # Edge-to-edge entry point & permission flow
│   ├── data/
│   │   ├── dao/CountdownDao.kt             # Room DAO queries & deduplication
│   │   ├── database/AppDatabase.kt         # Room database singleton & initial seed
│   │   ├── model/CountdownItem.kt          # Room entity model
│   │   ├── quotes/MotivationalQuotes.kt    # Curated bilingual quote repository
│   │   ├── repository/                     # Clean architecture repositories
│   │   └── weather/                        # Open-Meteo Retrofit service
│   ├── notification/
│   │   ├── NotificationHelper.kt           # Channel creation, exact alarms, test notification
│   │   ├── NotificationReceiver.kt         # BroadcastReceiver for alarms & rescheduling
│   │   └── BootReceiver.kt                 # Re-registers alarms on device reboot
│   └── ui/
│       ├── components/
│       │   ├── CinematicBackground.kt      # Lightweight GPU-friendly ambient canvas
│       │   ├── CountdownsSheet.kt          # M3 BottomSheet & Pure-Compose Editor
│       │   ├── ExpressiveCountdownDisplay.kt# Hero countdown card with direct edit button
│       │   ├── QuoteOfTheDaySection.kt     # Motivational quote card
│       │   ├── SettingsSheet.kt            # AMOLED/Dark theme & notification controls
│       │   └── TopBarSection.kt            # BST clock, Dhaka weather pill, sheet triggers
│       ├── screens/HomeScreen.kt           # Zero-delay main container
│       ├── theme/                          # Material 3 ColorScheme, Typography & Shapes
│       └── viewmodel/MainViewModel.kt      # Centralized state management
└── res/
    ├── drawable/ic_stat_notification.xml   # System-compliant monochrome vector icon
    ├── mipmap/                             # Adaptive launcher icons
    └── values/strings.xml
```

---

## 🚀 Getting Started & Building from Source

### Prerequisites
- **Android Studio:** Ladybug (2024.2.1+) or newer
- **JDK:** OpenJDK 17 or higher
- **Android SDK:** Platform 35 / 36 with Build-Tools installed

### Clone the Repository
```bash
git clone https://github.com/<your-username>/ssc27-countdown.git
cd ssc27-countdown
```

### Build & Run via Gradle
```bash
# Assemble Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest

# Install on connected device/emulator
./gradlew installDebug
```

The compiled APK will be located at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📲 Direct APK Download

Ready-to-install Android APK is available directly in this repository:

👉 **[Download SSC27.apk (v1.5)](download/SSC27.apk)**

> *Supports Android 10, 11, 12, 13, 14, and 15+.*

---

## 💡 Battery & Background Notification Tips (For Xiaomi / Samsung / Oppo)

On heavily customized Android skins (e.g. Xiaomi HyperOS/MIUI, Samsung OneUI, Realme UI), background tasks may be throttled by OEM battery management:

1. **Auto-Start:** Enable "Auto-start" for SSC 27 in your phone's App Info settings.
2. **Battery Saver:** Set battery optimization to **"No Restrictions"** to ensure periodic study reminders trigger on time.
3. **Lock Screen Visibility:** Ensure notifications are enabled for the lock screen in system settings.

---

## 🤝 Contributing

Contributions, feedback, and suggestions for SSC Batch 2027 students are warmly welcomed!

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

This project is open-source and distributed under the **Apache License 2.0**. See the `LICENSE` file for details.

---

<p align="center">
  <b>Designed with ❤️ for SSC Batch 2027 Candidates across Bangladesh 🇧🇩</b><br>
  <i>"Don't count the days, make the days count!"</i>
</p>
