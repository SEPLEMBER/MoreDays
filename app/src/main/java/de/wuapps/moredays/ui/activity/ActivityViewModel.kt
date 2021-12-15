package de.wuapps.moredays.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import kotlinx.coroutines.launch


class ActivityViewModel(
    private val activityDao: ActivityDao,
    _goal: Goal?,
    _activity: Activity?
) : ViewModel() {
    var isNew = _activity == null
    val activity = _activity ?: Activity()
    val goal = _goal!!
    val hasFab = false

    fun save() {
        if (isNew) {
            isNew = false
            viewModelScope.launch {
                activity.goalId = goal.uid
                activity.uid = activityDao.insert(activity)
            }
        } else {
            viewModelScope.launch {
                activityDao.update(activity)
            }
        }
    }

    fun delete() = viewModelScope.launch {
        activityDao.delete(activity)
    }

}