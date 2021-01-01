package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.ui.activity.ActivityViewModel

class ScaleViewModelFactory (
    private val scaleDao: ScaleDao,
    private val scale: Scale?
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScaleViewModel::class.java)) {
            return ScaleViewModel(scaleDao, scale) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
