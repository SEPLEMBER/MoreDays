package de.wuapps.moredays.ui.activity

import android.util.Log
import android.view.View
import android.widget.AdapterView
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
        get() {
            return goalSpinnerPosition.value?.let { goals.value?.get(it) }
        }
    var isUsed = false
    val goalSpinnerPosition = MutableLiveData<Int>()
    var initalSpinnerPosition = 0

    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        //pos                                 get selected item position
        //view.getText()                      get lable of selected item
        //parent.getAdapter().getItem(pos)    get item by pos
        //parent.getAdapter().getCount()      get item count
        //parent.getCount()                   get item count
        //parent.getSelectedItem()            get selected item
        //and other...
        Log.w("spinner", "onSelectItem $pos $id")
        if (initialized) {
            Log.w("spinner", "use given value $pos")
            goalSpinnerPosition.postValue(pos)
        }
//        else {
//            Log.w("spinner", "use initial value $initalSpinnerPosition")
//            goalSpinnerPosition.postValue(initalSpinnerPosition)
//            //parent?.run { setSelection(initalSpinnerPosition) }
//        }
        if (pos == 0)
            initialized = true //current implementation of spinner initializes it to first element after binding the view, hence this happens most likely after init here, hence ignore the first call
    }

    var initialized = false
    val goalId
        get() =
            goalSpinnerPosition.value?.let {
                goals?.value?.get(it)?.uid
            }


    init {
        Log.w("spinner","init vm")
            viewModelScope.launch {
            val activity = activityRepository.getActivityById(id)
            _activity.postValue(activity)
            //_activity.value = activityRepository.getActivityByName("abc")
            isUsed = activityRepository.isUsed(id)
            if (!isNew && activity != null) {
                Log.w("spinner", "goal id set in init")
                val position = goals?.value?.indexOfFirst {
                    it.uid == activity?.goalId
                } ?: -1
                if (position != -1) {
                    Log.w("spinner", "set in init to $position")
                    //goalSpinnerPosition.postValue(position)
                    goalSpinnerPosition.postValue(position)
                    initalSpinnerPosition = position
                }
            }
            else goalSpinnerPosition.value = 0
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