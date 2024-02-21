package de.wuapps.moredays.ui.tools.settings

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import de.wuapps.moredays.MainActivity

import de.wuapps.moredays.R
import de.wuapps.moredays.utilities.AlarmItem
import de.wuapps.moredays.utilities.AlarmReceiver
import de.wuapps.moredays.utilities.AndroidAlarmScheduler
import java.util.Calendar

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val defaultIsDarkMode = true
        const val REQUEST_CODE = 8
        const val NOTIFICATION_EXTRA = "extra"
    }

    private val hasNotificationPermission = false;
    private lateinit var sharedPrefs: SharedPreferences
    private var alarmItem: AlarmItem? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        sharedPrefs =
            PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext);
    }

    override fun onResume() {
        super.onResume()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.preferences_notification_hour_key) -> {
                updateNotifications()
            }

            getString(R.string.preferences_notification_minute_key) -> {
                updateNotifications()
            }

            getString(R.string.preferences_dark_mode_key) -> {
                hotReloadActivity()
            }

            getString(R.string.preferences_notification_key) -> {
                updateNotifications()
            }
        }
    }

    private fun updateNotifications() {
        Log.d("noti", "changed");
        val showNotification = sharedPrefs.getBoolean(
            requireContext().getString(R.string.preferences_notification_key),
            false
        )
        if (showNotification && askNotificationPermission()) {
            unsetAlarm()

            setAlarm()
        } else {
            unsetAlarm()
        }
    }

    private fun hotReloadActivity() {
        // Recreate doesn't support an animation, but any workaround is buggy
        val activity = requireActivity()
        ActivityCompat.recreate(activity)
    }

    private fun setAlarm() {
        Log.i("Alarm", "set alarm")
        val calendar: Calendar = Calendar.getInstance()
        val notificationHour =
            sharedPrefs.getString(
                requireContext().getString(R.string.preferences_notification_hour_key),
                "8"
            )!!
                .toInt()
        val notificationMinute = sharedPrefs.getString(
            requireContext().getString(R.string.preferences_notification_minute_key),
            "0"
        )!!.toInt()
        val text = sharedPrefs.getString(
            requireContext().getString(R.string.preferences_notification_text_key),
            requireContext().getString(R.string.settings_notification_text_default_value)
        ).toString()
        calendar.set(Calendar.HOUR_OF_DAY, notificationHour)
        calendar.set(Calendar.MINUTE, notificationMinute)
        calendar.set(Calendar.SECOND, 0)

        val (alarmManager, pendingIntent: PendingIntent) = getAlarmVars(text)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    private fun unsetAlarm(){
        val (alarmManager, pendingIntent: PendingIntent) = getAlarmVars("")
        alarmManager.cancel(pendingIntent)
    }
    private fun getAlarmVars(text: String): Pair<AlarmManager, PendingIntent> {
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(NOTIFICATION_EXTRA, text)
        }
           // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         val pendingIntent: PendingIntent =
             PendingIntent.getBroadcast(
                 context,
                 REQUEST_CODE,
                 intent,
                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
             )
        return Pair(alarmManager, pendingIntent)
    }


    // Ask notification permission
    private fun askNotificationPermission(code: Int = 201): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                code
            )
            return true
        } else return true
    }
}

