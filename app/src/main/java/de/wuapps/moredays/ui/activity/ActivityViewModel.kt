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
    private val _goals: LiveData<List<Goal>>,
    private val _activity: Activity?
) : BaseViewModel() {

//    val goals : LiveData<List<Goal>> = liveData {
//        goalRepository.allGoals
//            .onStart { isLoading.value = true }
//            .onCompletion {
//                isLoading.value = false
//            }
//            .collect { goals ->
//                if (!isNew){
//                    val position = goals.indexOfFirst {
//                        it.uid == _activity?.value?.goalId
//                    } ?: -1
//                    if (position != -1) {
//                        Log.w("spinner", "set in preselectSpinner to $position")
//                        goalSpinnerPosition.postValue(position)
//                    }
//                }
//                emit(goals) }
//    }

    val goals = _goals
    val isPossible: Boolean = goals.value?.isNotEmpty() == true
    val activity = _activity
    val isNew = activity!=null
    val goal: Goal?
        get() {
            return goalSpinnerPosition.value?.let { goals.value?.get(it) }
        }
    var isUsed = false
    var initialGoalId = -1L
    val goalSpinnerPosition = MutableLiveData<Int>()


    val goalId
        get() =
            goalSpinnerPosition.value?.let {
                goals?.value?.get(it)?.uid
            }

    init {
        Log.w("spinner", "init vm")
        viewModelScope.launch {
            if (activity != null)
            preselectSpinner()
        }
    }
    fun preselectSpinner(){
        if (!isNew && activity != null) {
            val count = goals.value?.size
            val goalid = activity.goalId
            Log.w("spinner", "goal id set in preselectSpinner, count goals: $count")
            Log.w("spinner", "search goalid $goalid")
            val position = goals?.value?.indexOfFirst {
                it.uid == activity.goalId
            } ?: -1
            if (position != -1) {
                Log.w("spinner", "set in preselectSpinner to $position")
                goalSpinnerPosition.postValue(position)
            }
        }
        else Log.w("spinner", "no preselection")
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