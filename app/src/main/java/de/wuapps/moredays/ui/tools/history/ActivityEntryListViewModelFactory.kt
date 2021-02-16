package de.wuapps.moredays.ui.tools.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityEntryDao

class ActivityEntryListViewModelFactory (private val activityEntryDao: ActivityEntryDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActivityEntryListViewModel(activityEntryDao) as T
    }
}
