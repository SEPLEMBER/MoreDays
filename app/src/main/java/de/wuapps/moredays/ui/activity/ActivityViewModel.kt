package de.wuapps.moredays.ui.activity

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val goalRepository: GoalRepository,
    id: Long
) : BaseViewModel() {
    val activities: LiveData<List<Activity>> = activityRepository.allActivitys.asLiveData()
    val goals = goalRepository.allGoals.asLiveData()
    val isPossible: Boolean = goals.value?.isNotEmpty() == true
    val isNew = id < 0
    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?>
        get() = _activity
    val goal: Goal?
        get() = goalSpinnerPosition.value?.let { goals?.value?.get(it) }
    var isUsed = false
    val goalSpinnerPosition = MutableLiveData<Int>()
//    var goalSpinnerPosition: Int
//        get() = _goalSpinnerPosition.value ?: 0
//        set(value) {
//            _goalSpinnerPosition.value = value
//            //_goal.value = goals?.value?.get(value)
//            //_activity.value?.goalId = _goal.value?.uid!!
//        }

    init {
        viewModelScope.launch {
            _activity.value = activityRepository.getActivityById(id)
            //_activity.value = activityRepository.getActivityByName("abc")
            isUsed = activityRepository.isUsed(id)
            if (!isNew) {
                val goalId = _activity.value?.goalId
                val position = goals?.value?.indexOfFirst {
                    it.uid == goalId
                } ?: -1
                if (position != -1) {
                    goalSpinnerPosition.value = position
                }
            }
        }
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(activity: Activity) = viewModelScope.launch {
        activityRepository.insert(activity)
    }

    fun update() = viewModelScope.launch {
        activityRepository.update(_activity.value!!)
    }

    fun delete() = viewModelScope.launch {
        activityRepository.delete(_activity.value!!)
    }

}