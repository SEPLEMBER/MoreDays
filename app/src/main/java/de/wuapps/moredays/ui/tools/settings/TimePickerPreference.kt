package de.wuapps.moredays.ui.tools.settings

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat.is24HourFormat
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.PreferenceTimePickerBinding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// thanks to https://github.com/m-i-n-a-r/birday
class TimePickerPreference(context: Context?, attrs: AttributeSet?) : Preference(context!!, attrs),
    View.OnClickListener {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var currentHour: String
    private lateinit var currentMinute: String
    private lateinit var binding: PreferenceTimePickerBinding
    val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        currentHour = sharedPrefs.getString(context.getString(R.string.preferences_notification_hour_key), "8").toString()
        currentMinute = sharedPrefs.getString(context.getString(R.string.preferences_notification_minute_key), "0").toString()
        super.onBindViewHolder(holder)
        binding = PreferenceTimePickerBinding.bind(holder.itemView)

        binding.textViewTime.text = String.format("%02d:%02d", currentHour.toInt(), currentMinute.toInt())

        binding.root.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val act = context as MainActivity
        currentHour = sharedPrefs.getString(context.getString(R.string.preferences_notification_hour_key), "8").toString()
        currentMinute = sharedPrefs.getString(context.getString(R.string.preferences_notification_minute_key), "0").toString()

        // Show the time picker
        val isSystem24Hour = is24HourFormat(context)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(currentHour.toInt())
                .setMinute(currentMinute.toInt())
                .setTitleText(context.getString(R.string.settings_notification_time_title))
                .build()

        picker.addOnPositiveButtonClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("notification_hour", "${picker.hour}")
            editor.putString("notification_minute", "${picker.minute}")
            editor.apply()

            binding.textViewTime.text = String.format("%02d:%02d", picker.hour, picker.minute)
        }

        picker.show(act.supportFragmentManager, "timepicker")
    }

}