package de.wuapps.moredays.ui.tools.trophy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.TrophyDao

class TrophyListViewModelFactory (private val trophyDao: TrophyDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrophyListViewModel(trophyDao) as T
    }
}
