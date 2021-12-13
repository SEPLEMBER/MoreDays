package de.wuapps.moredays

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.maltaisn.icondialog.pack.IconPack
import de.wuapps.moredays.ui.tools.settings.SettingsFragment


class MoreDaysApplication : Application() {

    companion object {
        var iconPack: IconPack? = null
        var loaded : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    }
    init {
        loaded.value = false
    }

    override fun onCreate() { //instantiation requires android:name=".MoreDaysApplication" in the manifest
        super.onCreate()
        handlePreferedTheme()
    }

    private fun handlePreferedTheme(){ //see https://github.com/googlearchive/android-DarkTheme
        val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean(getString(R.string.preferences_dark_mode_key), SettingsFragment.defaultIsDarkMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }


}