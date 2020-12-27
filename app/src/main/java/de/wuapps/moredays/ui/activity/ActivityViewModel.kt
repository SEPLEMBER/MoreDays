package de.wuapps.moredays.ui.activity

import android.util.Log
import androidx.lifecycle.*
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch


class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val _goal: Goal,
    private val _activity: Activity?
) : BaseViewModel() {
    val activity = _activity
    val goal = _goal
    val isNew = activity==null
    var isUsed = false

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(activity: Activity) = viewModelScope.launch {
        activityRepository.insert(activity)
    }

    fun update() = viewModelScope.launch {

            activityRepository.update(activity!!)

    }

    fun delete() = viewModelScope.launch {
        activityRepository.delete(activity!!)
    }

}