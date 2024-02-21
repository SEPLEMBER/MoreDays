package de.wuapps.moredays.utilities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.tools.settings.SettingsFragment.Companion.NOTIFICATION_EXTRA

// thanks to https://medium.com/@arfinhosain/android-alarmmanager-scheduling-alarms-and-displaying-notifications-77f815e2638
class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(NOTIFICATION_EXTRA) ?: return

        notificationManager = NotificationManagerCompat.from(context!!)
        val notification = NotificationCompat.Builder(context, context.getString(R.string.app_name))
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setSmallIcon(R.drawable.splash_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
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