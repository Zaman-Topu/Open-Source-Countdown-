package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.quotes.MotivationalQuotes
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object NotificationHelper {

    const val CHANNEL_MOTIVATION_5H = "ssc27_channel_motivation_5h"
    const val CHANNEL_COUNTDOWN_12H = "ssc27_channel_countdown_12h"
    const val CHANNEL_BENGALI_24H = "ssc27_channel_bengali_24h"

    const val ACTION_ALARM_5H = "com.example.notification.ALARM_5H"
    const val ACTION_ALARM_12H = "com.example.notification.ALARM_12H"
    const val ACTION_ALARM_24H = "com.example.notification.ALARM_24H"

    private const val PREFS_NAME = "ssc27_notification_prefs"
    private const val KEY_NOTIF_ENABLED = "key_notif_enabled"
    private const val KEY_5H_INDEX = "key_5h_index"
    private const val KEY_24H_INDEX = "key_24h_index"
    private const val KEY_ACTIVE_TITLE = "key_active_title"
    private const val KEY_ACTIVE_TARGET_MILLIS = "key_active_target_millis"

    fun updateActiveCountdown(context: Context, title: String, targetEpochMillis: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_ACTIVE_TITLE, title)
            .putLong(KEY_ACTIVE_TARGET_MILLIS, targetEpochMillis)
            .apply()
    }

    fun getActiveCountdown(context: Context): Pair<String, Long> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dhakaZone = ZoneId.of("Asia/Dhaka")
        val defaultTarget = ZonedDateTime.of(2027, 1, 7, 10, 0, 0, 0, dhakaZone).toInstant().toEpochMilli()
        val title = prefs.getString(KEY_ACTIVE_TITLE, "SSC 27") ?: "SSC 27"
        val targetMillis = prefs.getLong(KEY_ACTIVE_TARGET_MILLIS, defaultTarget)
        return Pair(title, targetMillis)
    }

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel5h = NotificationChannel(
                CHANNEL_MOTIVATION_5H,
                "5-Hour Motivation (Bangla + English)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Periodic short motivational boosts for SSC 27"
                enableVibration(true)
            }

            val channel12h = NotificationChannel(
                CHANNEL_COUNTDOWN_12H,
                "12-Hour Countdown Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active countdown days and hours remaining"
                enableVibration(true)
            }

            val channel24h = NotificationChannel(
                CHANNEL_BENGALI_24H,
                "24-Hour Bengali Daily Inspiration",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily profound Bengali wisdom and encouragement"
            }

            notificationManager.createNotificationChannel(channel5h)
            notificationManager.createNotificationChannel(channel12h)
            notificationManager.createNotificationChannel(channel24h)
        }
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIF_ENABLED, true)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
        if (enabled) {
            scheduleAllAlarms(context)
        } else {
            cancelAllAlarms(context)
        }
    }

    fun scheduleAllAlarms(context: Context) {
        if (!isNotificationsEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 5 hours alarm
        val interval5h = 5 * 60 * 60 * 1000L
        val intent5h = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_ALARM_5H
        }
        val pi5h = PendingIntent.getBroadcast(
            context,
            1001,
            intent5h,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval5h,
            interval5h,
            pi5h
        )

        // 12 hours countdown reminder
        val interval12h = 12 * 60 * 60 * 1000L
        val intent12h = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_ALARM_12H
        }
        val pi12h = PendingIntent.getBroadcast(
            context,
            1002,
            intent12h,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval12h,
            interval12h,
            pi12h
        )

        // 24 hours Bengali motivation
        val interval24h = 24 * 60 * 60 * 1000L
        val intent24h = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_ALARM_24H
        }
        val pi24h = PendingIntent.getBroadcast(
            context,
            1003,
            intent24h,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval24h,
            interval24h,
            pi24h
        )
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val pi5h = PendingIntent.getBroadcast(
            context,
            1001,
            Intent(context, NotificationReceiver::class.java).apply { action = ACTION_ALARM_5H },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pi12h = PendingIntent.getBroadcast(
            context,
            1002,
            Intent(context, NotificationReceiver::class.java).apply { action = ACTION_ALARM_12H },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pi24h = PendingIntent.getBroadcast(
            context,
            1003,
            Intent(context, NotificationReceiver::class.java).apply { action = ACTION_ALARM_24H },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pi5h)
        alarmManager.cancel(pi12h)
        alarmManager.cancel(pi24h)
    }

    fun triggerTestNotification(context: Context) {
        val quotes = MotivationalQuotes.quotes
        val quote = quotes.random()

        val (activeTitle, activeTargetMillis) = getActiveCountdown(context)
        val nowMillis = System.currentTimeMillis()
        val diffMillis = activeTargetMillis - nowMillis
        val days = (diffMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        val hours = ((diffMillis / (1000 * 60 * 60)) % 24).coerceAtLeast(0)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remainingText = if (diffMillis > 0) "আর মাত্র $days দিন $hours ঘণ্টা বাকি!" else "মাহেন্দ্রক্ষণ উপস্থিত!"

        val notification = NotificationCompat.Builder(context, CHANNEL_COUNTDOWN_12H)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$activeTitle · $days Days Remaining")
            .setContentText(quote.bangla)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${quote.bangla}\n\n\"${quote.english}\"\n\n$remainingText")
                    .setSummaryText(activeTitle)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(9001, notification)
        } catch (_: SecurityException) {
            // Android 13+ permission not yet granted
        }
    }

    fun getNextQuoteFor5H(context: Context): com.example.data.quotes.Quote {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(KEY_5H_INDEX, 0)
        val quotes = MotivationalQuotes.quotes
        val quote = quotes[currentIndex % quotes.size]
        prefs.edit().putInt(KEY_5H_INDEX, (currentIndex + 1) % quotes.size).apply()
        return quote
    }

    fun getNextQuoteFor24H(context: Context): com.example.data.quotes.Quote {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(KEY_24H_INDEX, 0)
        val quotes = MotivationalQuotes.quotes
        val quote = quotes[currentIndex % quotes.size]
        prefs.edit().putInt(KEY_24H_INDEX, (currentIndex + 1) % quotes.size).apply()
        return quote
    }
}
