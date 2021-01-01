package de.wuapps.moredays.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal


class ActivityViewModelFactory
    (
    private val activityDao: ActivityDao,
    private val goal: Goal,
    private val activity: Activity?
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            return ActivityViewModel(activityDao, goal, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
