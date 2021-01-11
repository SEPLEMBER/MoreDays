package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ScaleDao

class ScaleListViewModelFactory(private val scaleDao: ScaleDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScaleListViewModel::class.java)) {
            return ScaleListViewModel(scaleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
