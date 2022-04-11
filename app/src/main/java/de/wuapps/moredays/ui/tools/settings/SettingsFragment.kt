package de.wuapps.moredays.ui.tools.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.wuapps.moredays.R

class SettingsFragment : PreferenceFragmentCompat() {
    companion object{
        const val defaultIsDarkMode = true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}