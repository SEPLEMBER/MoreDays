package de.wuapps.moredays.ui.tools.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.TrophyDao

class ActivityEntryListViewModelFactory (private val activityEntryDao: ActivityEntryDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityEntryListViewModel::class.java)) {
            return ActivityEntryListViewModel(activityEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
