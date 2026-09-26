package com.example.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!NotificationHelper.isNotificationsEnabled(context)) return

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            (System.currentTimeMillis() % 10000).toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = NotificationManagerCompat.from(context)

        when (intent.action) {
            NotificationHelper.ACTION_ALARM_5H -> {
                val quote = NotificationHelper.getNextQuoteFor5H(context)
                val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_MOTIVATION_5H)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("SSC 27 · প্রেরণা ও ফোকাস")
                    .setContentText(quote.bangla)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("${quote.bangla}\n\n\"${quote.english}\"")
                            .setSummaryText("5-Hour Motivation")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                try {
                    notificationManager.notify(5001, notification)
                } catch (_: SecurityException) {}
            }

            NotificationHelper.ACTION_ALARM_12H -> {
                val (titleText, targetMillis) = NotificationHelper.getActiveCountdown(context)
                val nowMillis = System.currentTimeMillis()
                val diff = targetMillis - nowMillis

                val days = (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                val hours = ((diff / (1000 * 60 * 60)) % 24).coerceAtLeast(0)

                val title = if (days > 0 || hours > 0) {
                    "$titleText · আর মাত্র $days দিন $hours ঘণ্টা বাকি!"
                } else {
                    "$titleText · পরীক্ষার মাহেন্দ্রক্ষণ উপস্থিত!"
                }

                val body = "তোমার স্বপ্নপূরণের লক্ষ্যমাত্রা দিন দিন এগিয়ে আসছে। আজ প্রতিটি মিনিটকে কাজে লাগাও!"

                val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_COUNTDOWN_12H)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("$body\n\n\"Don't count the days, make the days count!\"")
                            .setSummaryText(titleText)
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                try {
                    notificationManager.notify(12001, notification)
                } catch (_: SecurityException) {}
            }

            NotificationHelper.ACTION_ALARM_24H -> {
                val quote = NotificationHelper.getNextQuoteFor24H(context)
                val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_BENGALI_24H)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("SSC 27 · দৈনিক অনুপ্রেরণা")
                    .setContentText(quote.bangla)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("${quote.bangla}\n\n— ${quote.author}")
                            .setSummaryText("Daily Wisdom")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                try {
                    notificationManager.notify(24001, notification)
                } catch (_: SecurityException) {}
            }
        }
    }
}
