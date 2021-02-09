package de.wuapps.moredays

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.utilities.SingletonHolder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_tools, R.id.navigation_journal,
                R.id.navigation_placeholder, R.id.navigation_home, R.id.navigation_charts
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.menu.getItem(2).isEnabled = false
        iconPack = IconManager.getInstance(this.application).iconPack
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