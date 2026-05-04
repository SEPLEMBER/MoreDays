package de.wuapps.moredays.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.dao.ScaleEntryDao
import de.wuapps.moredays.database.entity.ScaleEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScalesChartViewModel(
    private val scaleEntryDao: ScaleEntryDao,
    private val scaleDao: ScaleDao
) : ViewModel() {
    val scaleData = scaleEntryDao.getAllScaleData().asLiveData()
    val scales = scaleDao.getAll().asLiveData()

    fun removeScaleEntry(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            scaleEntryDao.delete(id)
        }
    }

    fun addScaleEntry(scaleId: Long, value: Float){
        viewModelScope.launch(Dispatchers.IO){
            val scaleEntry = ScaleEntry(scaleId, value)
            scaleEntryDao.insert(scaleEntry)
        }
    }
}
