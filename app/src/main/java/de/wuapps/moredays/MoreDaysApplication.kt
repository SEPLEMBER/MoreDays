package de.wuapps.moredays

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.ui.tools.settings.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MoreDaysApplication : Application() {

    companion object {
        var iconPack: IconPack? = null
        var loaded : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        const val CHANNEL_ID = "reminder"
    }
    init {
        loaded.value = false
    }

    override fun onCreate() { //instantiation requires android:name=".MoreDaysApplication" in the manifest
        super.onCreate()
        handlePreferredTheme()
        createNotificationChannel()
        loadIconPack()
    }

    private fun loadIconPack() {
        if (iconPack != null) {
            // Icon pack is already loaded.
            return
        }
        val iconPackLoader = IconPackLoader(this)
        CoroutineScope(Dispatchers.Default).launch(Dispatchers.IO) {
            MoreDaysApplication.iconPack = withContext(Dispatchers.Default) {
                val pack = createDefaultIconPack(iconPackLoader)
                pack.loadDrawables(iconPackLoader.drawableLoader)
                loaded.postValue(true)
                pack
            }
        }
    }
    private fun handlePreferredTheme(){ //see https://github.com/googlearchive/android-DarkTheme
        val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(getString(R.string.preferences_dark_mode_key), SettingsFragment.defaultIsDarkMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.app_name) + " Channel"
        val descriptionText = getString(R.string.app_name) + " Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}