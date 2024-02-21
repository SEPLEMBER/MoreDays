package de.wuapps.moredays.utilities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.wuapps.moredays.ui.tools.settings.SettingsFragment.Companion.ALARM_REQUEST_CODE
import java.time.LocalDateTime
import java.time.ZoneId

data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}

// thanks to https://medium.com/@arfinhosain/android-alarmmanager-scheduling-alarms-and-displaying-notifications-77f815e2638
class AndroidAlarmScheduler(
private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("extra", item.message)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}