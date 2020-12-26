package de.wuapps.moredays.ui.icons

import android.app.Application
import androidx.lifecycle.ViewModel
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import de.wuapps.moredays.ui.BaseViewModel

class IconViewModel(application: Application) : BaseViewModel() {
    var iconPack: IconPack

    init {
        // Create an icon pack loader with application context.
        val loader = IconPackLoader(application)
        // Create an icon pack and load all drawables.
        iconPack = createDefaultIconPack(loader)
        iconPack.loadDrawables(loader.drawableLoader)

    }
}