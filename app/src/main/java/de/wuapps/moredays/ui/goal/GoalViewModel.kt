package de.wuapps.moredays.ui.goal

import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.BaseViewModel
import kotlinx.coroutines.launch

class GoalViewModel(private val goalRepository: GoalRepository, private val _goal: Goal?) :
    BaseViewModel() {
    var isNew = _goal == null
    var goal = _goal
    var isUsed = false

    init {
        viewModelScope.launch {
            if (goal != null)
                isUsed = goalRepository.isUsed(goal!!.uid)
        }
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(pGoal: Goal) = viewModelScope.launch {
        pGoal.uid = goalRepository.insert(pGoal)
        goal = pGoal
        isNew = false
    }

    fun update() = viewModelScope.launch {
        goalRepository.update(goal!!)
    }

    fun delete() = viewModelScope.launch {
        goalRepository.delete(goal!!)
    }

    fun filterLinkedActivites(activities: List<Activity>): List<Activity> {
        if (goal != null)
            return activities.filter { activity -> activity.goalId == goal!!.uid }
        return activities
    }
}