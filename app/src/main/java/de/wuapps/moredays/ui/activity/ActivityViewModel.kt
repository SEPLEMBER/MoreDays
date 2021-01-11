package de.wuapps.moredays.ui.activity

import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch


class ActivityViewModel(
    private val activityDao: ActivityDao,
    _goal: Goal,
    _activity: Activity?
) : BaseViewModel() {
    val activity = _activity
    val goal = _goal
    val isNew = activity == null
    var isUsed = false

    init {
        viewModelScope.launch {
            if (activity != null)
                isUsed = activityDao.isUsed(activity.uid) > 0
        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(activity: Activity) = viewModelScope.launch {
        activityDao.insert(activity)
    }

    fun update() = viewModelScope.launch {
        activityDao.update(activity!!)
    }

    fun delete() = viewModelScope.launch {
        activityDao.delete(activity!!)
    }

}