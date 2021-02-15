package de.wuapps.moredays

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.maltaisn.icondialog.pack.IconPack

class MoreDaysApplication : Application() {

    companion object {
        var iconPack: IconPack? = null
        var loaded : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    }
    init {
        MoreDaysApplication.loaded.value = false
    }
}