package de.wuapps.moredays.ui.goal

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch

class GoalViewModel(private val goalRepository: GoalRepository, id: Long) : BaseViewModel() {
    var isNew = id < 0
    private val _goal = MutableLiveData<Goal?>()
    val goal: LiveData<Goal?>
        get() = _goal
    var isUsed = false
    init {
        viewModelScope.launch {
            _goal.value = goalRepository.getGoalById(id)
            //_goal.value = goalRepository.getGoalByName("abc")
            isUsed = goalRepository.isUsed(id)
        }
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(goal: Goal) = viewModelScope.launch {
        goal.uid = goalRepository.insert(goal)
        _goal.value = goal
        isNew = false
    }

    fun update() = viewModelScope.launch {
        goalRepository.update(_goal.value!!)
    }

    fun delete() = viewModelScope.launch {
        goalRepository.delete(_goal.value!!)
    }

}