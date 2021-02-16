package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.Goal

class GoalViewModelFactory
    (private val goalDao: GoalDao, private val goal: Goal?) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalViewModel(goalDao, goal) as T
    }
}
