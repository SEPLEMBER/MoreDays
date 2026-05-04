package de.wuapps.moredays

import android.app.Application
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.app.imagepickerlibrary.ui.activity.ImagePickerActivity
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import com.yalantis.ucrop.UCropActivity
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
        registerPickerInsetsHandling()
        createNotificationChannel()
        loadIconPack()
    }

    private fun registerPickerInsetsHandling() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ImagePickerActivity || activity.javaClass.name == "com.yalantis.ucrop.UCropActivity") {
                    // Android 15/16 can force third-party activities edge-to-edge. The picker
                    // library does not consume insets itself, so keep these screens in the
                    // classic system-window mode to avoid overlap with status/navigation bars.
                    WindowCompat.setDecorFitsSystemWindows(activity.window, true)
                }
                if (activity is UCropActivity) {
                    installUCropInsetsHandling(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    private fun installUCropInsetsHandling(activity: UCropActivity) {
        activity.window.decorView.post {
            val root = activity.findViewById<android.view.View>(com.yalantis.ucrop.R.id.ucrop_photobox) ?: return@post
            val toolbar = activity.findViewById<android.view.View>(com.yalantis.ucrop.R.id.toolbar)
            val controls = activity.findViewById<android.view.View>(com.yalantis.ucrop.R.id.controls_wrapper)
            val toolbarTopPadding = toolbar?.paddingTop ?: 0
            val controlsBottomPadding = controls?.paddingBottom ?: 0

            ViewCompat.setOnApplyWindowInsetsListener(root) { _, windowInsets ->
                val statusInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                toolbar?.updatePadding(top = toolbarTopPadding + statusInsets.top)
                controls?.updatePadding(bottom = controlsBottomPadding + navInsets.bottom)
                windowInsets
            }
            ViewCompat.requestApplyInsets(root)
        }
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
