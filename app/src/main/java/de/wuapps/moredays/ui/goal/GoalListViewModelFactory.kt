package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.GoalDao

class GoalListViewModelFactory
    (private val goalDao: GoalDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalListViewModel::class.java)) {
            return GoalListViewModel(goalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
