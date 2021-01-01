package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.GoalDao

class GoalListViewModel(private val goalDao: GoalDao): ViewModel() {
    val goals = goalDao.getAll().asLiveData()
}