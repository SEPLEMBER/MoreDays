package de.wuapps.moredays

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.utilities.SingletonHolder

class MainActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        fab = findViewById(R.id.fabMain)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_tools, R.id.navigation_journal,
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
        iconPack = IconManager.getInstance(this.application).iconPack
    }

    fun getFab(): FloatingActionButton{
        return fab
    }
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    companion object  {
        var iconPack: IconPack? = null
    }

    class IconManager private constructor(application: Application) {
        var iconPack: IconPack

        init {
            // Create an icon pack loader with application context.
            val loader = IconPackLoader(application)
            // Create an icon pack and load all drawables.
            iconPack = createDefaultIconPack(loader)
            iconPack.loadDrawables(loader.drawableLoader)
        }

        companion object : SingletonHolder<IconManager, Application>(::IconManager)
    }
}