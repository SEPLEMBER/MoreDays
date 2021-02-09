package de.wuapps.moredays.ui.tools.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.TrophyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityEntryListViewModel(private val activityEntryDao: ActivityEntryDao) : ViewModel() {
    val activityEntries = activityEntryDao.getAllAndRelatedData().asLiveData()

    fun removeActivityEntry(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            activityEntryDao.delete(id)
        }
    }
}