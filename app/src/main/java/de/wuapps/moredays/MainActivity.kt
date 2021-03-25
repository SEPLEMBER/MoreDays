package de.wuapps.moredays

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.ui.onboarding.Onboarding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private var bottomAppBarVerticalOffsetOrg = 0F
    private val app: MoreDaysApplication
        get() = this.application as MoreDaysApplication
    private lateinit var iconPackLoader: IconPackLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iconPackLoader = IconPackLoader(this)
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE)  //getPreferences(MODE_PRIVATE)
        if (!sharedPref.getBoolean(getString(R.string.preferences_onboarding), false)) {
            val intentOnboarding = Intent(this, Onboarding::class.java)
            startActivity(intentOnboarding)
        }
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        fab = findViewById(R.id.fabMain)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        bottomAppBarVerticalOffsetOrg = bottomAppBar.cradleVerticalOffset
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_tools, R.id.navigation_journalList,
                R.id.navigation_placeholder, R.id.navigation_home, R.id.navigation_charts
            )
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.menu.getItem(4).isEnabled = false
        navView.background = null
        loadIconPack()
    }

    fun getFab(): FloatingActionButton {
        return fab
    }

    fun hideFab() {
        fab.hide()
        bottomAppBar.cradleVerticalOffset = 0F
    }

    fun showFab() {
        fab.show()
        bottomAppBar.cradleVerticalOffset = bottomAppBarVerticalOffsetOrg
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    private fun loadIconPack() {
        if (MoreDaysApplication.iconPack != null) {
            // Icon pack is already loaded.
            return
        }
        MoreDaysApplication.iconPack = null
        // Start new job to load icon pack.
        lifecycleScope.launch(Dispatchers.IO) {
            MoreDaysApplication.iconPack = withContext(Dispatchers.Default) {
                val pack = createDefaultIconPack(iconPackLoader)
                pack.loadDrawables(iconPackLoader.drawableLoader)
                MoreDaysApplication.loaded.postValue(true)
                pack
            }
        }
    }
}