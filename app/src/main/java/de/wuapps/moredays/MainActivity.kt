package de.wuapps.moredays

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.ui.onboarding.Onboarding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), ImagePickerResultListener {
    private lateinit var fab: FloatingActionButton
    private lateinit var fragmentContainerView : FragmentContainerView
    private var fragmentContainerPaddingBottom = 0
    private lateinit var bottomAppBar: BottomAppBar
    private var bottomAppBarVerticalOffsetOrg = 0F
    private lateinit var iconPackLoader: IconPackLoader
    // work around as instance of imagepicker is registered in its constructor ...
    // when I used the imagePicker in the fragment I got the error java.lang.IllegalArgumentException: SavedStateProvider with the given key is already registered
    public val imagePicker: ImagePicker by lazy {
        registerImagePicker(this@MainActivity)
    }
    private var imagePickerCallback : ((Uri) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        iconPackLoader = IconPackLoader(this)
        loadIconPack()

        //handleOnBoarding()

        fab = findViewById(R.id.fabMain)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        bottomAppBarVerticalOffsetOrg = bottomAppBar.cradleVerticalOffset
        fragmentContainerView = findViewById(R.id.nav_host_fragment)
        fragmentContainerPaddingBottom = fragmentContainerView.paddingBottom

        wireUpNavigation()
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
        navView.menu.getItem(4).isEnabled = false
        navView.background = null
    }



    fun getFab(): FloatingActionButton {
        return fab
    }

    fun hideFab() {
        fab.hide()
        bottomAppBar.cradleVerticalOffset = 0F
        fragmentContainerView.setPadding(fragmentContainerView.paddingLeft, fragmentContainerView.paddingTop, fragmentContainerView.paddingRight, 0)
    }

    fun showFab() {
        fab.show()
        bottomAppBar.cradleVerticalOffset = bottomAppBarVerticalOffsetOrg
        fragmentContainerView.setPadding(fragmentContainerView.paddingLeft, fragmentContainerView.paddingTop, fragmentContainerView.paddingRight, fragmentContainerPaddingBottom)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    public fun setImagePickerCallback(callback: (Uri) -> Unit){
        imagePickerCallback = callback
    }

    public fun unsetImagePickerCallback(){
        imagePickerCallback = null
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

    override fun onImagePick(uri: Uri?) {
        if (uri != null)
            imagePickerCallback?.let { it(uri) }
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        //not used
    }
}