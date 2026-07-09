package de.wuapps.moredays

import android.content.Intent
import android.view.View
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.wuapps.moredays.ui.onboarding.Onboarding

class MainActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private lateinit var fragmentContainerView : FragmentContainerView
    private lateinit var bottomAppBar: BottomAppBar
    private var bottomAppBarVerticalOffsetOrg = 0F
    private var bottomAppBarPaddingBottom = 0
    private var systemBarInsetBottom = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        handleOnBoarding()

        val container: androidx.coordinatorlayout.widget.CoordinatorLayout = findViewById(R.id.container)
        fab = findViewById(R.id.fabMain)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        bottomAppBarVerticalOffsetOrg = bottomAppBar.cradleVerticalOffset
        bottomAppBarPaddingBottom = bottomAppBar.paddingBottom
        fragmentContainerView = findViewById(R.id.nav_host_fragment)

        wireUpNavigation()

        ViewCompat.setOnApplyWindowInsetsListener(container) { view, windowInsets ->
            val statusInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            systemBarInsetBottom = navInsets.bottom
            view.updatePadding(top = statusInsets.top)
            bottomAppBar.updatePadding(bottom = bottomAppBarPaddingBottom + systemBarInsetBottom)
            bottomAppBar.post { updateFragmentContainerBottomPadding() }
            windowInsets
        }
        ViewCompat.requestApplyInsets(container)
    }

    private fun handleOnBoarding(){
        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (!sharedPref.getBoolean(getString(R.string.preferences_onboarding), false)) {
            val intentOnboarding = Intent(this, Onboarding::class.java)
            startActivity(intentOnboarding)
            finish()
        }
    }

    private fun wireUpNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
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
        navView.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) {
                true
            } else {
                val navOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.nav_enter)
                    .setExitAnim(R.anim.nav_exit)
                    .setPopEnterAnim(R.anim.nav_enter)
                    .setPopExitAnim(R.anim.nav_exit)
                    .setPopUpTo(navController.graph.startDestinationId, false, true)
                    .build()
                try {
                    navController.navigate(item.itemId, null, navOptions)
                    true
                } catch (_: IllegalArgumentException) {
                    false
                }
            }
        }
        navView.setOnItemReselectedListener { }
        navView.menu[4].isEnabled = false
        navView.background = null
    }



    fun getFab(): FloatingActionButton {
        return fab
    }

    fun hideBottomBar() {
        bottomAppBar.visibility = View.GONE
        updateFragmentContainerBottomPadding()
    }

    fun showBottomBar() {
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.post { updateFragmentContainerBottomPadding() }
    }

    fun hideFab() {
        fab.hide()
        bottomAppBar.cradleVerticalOffset = 0F
    }

    fun showFab() {
        fab.show()
        bottomAppBar.cradleVerticalOffset = bottomAppBarVerticalOffsetOrg
    }

    private fun updateFragmentContainerBottomPadding() {
        val bottomBarPadding = if (bottomAppBar.visibility == View.VISIBLE) {
            bottomAppBar.height
        } else {
            systemBarInsetBottom
        }
        fragmentContainerView.updatePadding(bottom = bottomBarPadding)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}
