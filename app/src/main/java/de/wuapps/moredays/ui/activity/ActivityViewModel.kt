package de.wuapps.moredays.ui.activity

import androidx.lifecycle.*
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch


class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    _goal: Goal,
    _activity: Activity?
) : BaseViewModel() {
    val activity = _activity
    val goal = _goal
    val isNew = activity==null
    var isUsed = false

    init {
        viewModelScope.launch {
            if (activity != null)
                isUsed = activityRepository.isUsed(activity.uid)
        }
    }

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