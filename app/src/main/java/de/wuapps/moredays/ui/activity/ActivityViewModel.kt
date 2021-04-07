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
    val activity = _activity
    val goal = _goal!!
    val isNew = activity == null
    val hasFab = false

    fun insert(activity: Activity) = viewModelScope.launch {
        activityDao.insert(activity)
    }

    fun update() = viewModelScope.launch {
        activityDao.update(activity!!)
    }

    fun delete() = viewModelScope.launch {
        activityDao.deleteActivityAndRelatedChallenges(activity!!)
    }

}