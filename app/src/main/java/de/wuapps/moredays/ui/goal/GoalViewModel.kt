package de.wuapps.moredays.ui.goal

import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch

class GoalViewModel(private val goalRepository: GoalRepository, _goal: Goal?) :
    BaseViewModel() {
    var isNew = _goal == null
    lateinit var goal:Goal
    var isUsed = false

    init {
        if (_goal == null){
            isNew = true
            goal = Goal()
        }
        else {
            goal = _goal
        }
        viewModelScope.launch {
            if (!isNew)
                isUsed = goalRepository.isUsed(goal!!.uid)
        }
    }

    fun save(){
        if (isNew){
            isNew = false
            viewModelScope.launch {
                goal!!.uid = goalRepository.insert(goal!!)
            }
        }
        else {
            viewModelScope.launch {
                goalRepository.update(goal!!)
            }
        }
    }

    fun delete() = viewModelScope.launch {
        goalRepository.delete(goal!!)
        goal = Goal()
        isNew = true
    }

    fun filterLinkedActivites(activities: List<Activity>): List<Activity> {
        if (goal != null && goal!!.uid != 0L)
            return activities.filter { activity -> activity.goalId == goal!!.uid }
        return activities
    }
}