package de.wuapps.moredays.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.dao.ScaleEntryDao

class ScalesChartViewModelFactory (private val scaleDao: ScaleDao, private val scaleEntryDao: ScaleEntryDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScalesChartViewModel(scaleDao, scaleEntryDao) as T
    }
}