package de.wuapps.moredays.ui.tools.settings

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import de.wuapps.moredays.R
import de.wuapps.moredays.utilities.AlarmReceiver
import java.util.Calendar

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val defaultIsDarkMode = true
        const val ALARM_REQUEST_CODE = 8
        const val NOTIFICATION_EXTRA = "extra"
    }

    private lateinit var sharedPrefs: SharedPreferences
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

        val currentTime = System.currentTimeMillis()
        // If the desired time has already passed, add a day to the calendar
        if (calendar.timeInMillis <= currentTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        // Calculate the delay until the next alarm
        val delay = calendar.timeInMillis - currentTime

        val (alarmManager, pendingIntent: PendingIntent) = getAlarmVars(text)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            currentTime + delay,
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
         val pendingIntent: PendingIntent =
             PendingIntent.getBroadcast(
                 context,
                 ALARM_REQUEST_CODE,
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

