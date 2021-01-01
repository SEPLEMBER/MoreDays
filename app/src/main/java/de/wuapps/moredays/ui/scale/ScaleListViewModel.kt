package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.ScaleDao

class ScaleListViewModel (private val scaleDao: ScaleDao): ViewModel() {
    val scales = scaleDao.getAll().asLiveData()
}