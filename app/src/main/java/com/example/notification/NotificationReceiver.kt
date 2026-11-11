package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            NotificationHelper.ACTION_DAILY_CHECKIN_ALARM -> {
                NotificationHelper.showDailyCheckInNotification(context)
                // Schedule the next day's alarm
                if (NotificationHelper.isDailyCheckInEnabled(context)) {
                    NotificationHelper.scheduleDailyReminder(context)
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                if (NotificationHelper.isDailyCheckInEnabled(context)) {
                    NotificationHelper.scheduleDailyReminder(context)
                }
            }
        }
    }
}
