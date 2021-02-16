package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Scale

class ScaleViewModelFactory(
    private val scaleDao: ScaleDao,
    private val scale: Scale?
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScaleViewModel(scaleDao, scale) as T
    }
}
