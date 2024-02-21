package de.wuapps.moredays.utilities

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.tools.settings.SettingsFragment.Companion.NOTIFICATION_EXTRA

// thanks to https://medium.com/@arfinhosain/android-alarmmanager-scheduling-alarms-and-displaying-notifications-77f815e2638
class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context?, intent: Intent?) {
//        if (intent == null || context == null) return

        val message = intent?.getStringExtra("extra") ?: return
        notificationManager = NotificationManagerCompat.from(context!!)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "default")
            .setContentText(message)
            .setSmallIcon(R.drawable.splash_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) //automatically removes the notification when the user taps it
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager?.notify(1, notification)
    }
}