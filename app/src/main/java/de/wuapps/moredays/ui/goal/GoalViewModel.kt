package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.HundredPercentValueDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.HundredPercentValue
import kotlinx.coroutines.launch

class GoalViewModel(private val goalDao: GoalDao, private val hundredPercentValueDao: HundredPercentValueDao,  _goal: Goal?) :
    ViewModel() {
    var isNew = _goal == null
    var goal = _goal ?: Goal()
    val hasFab = false
    val goalAndRelatedActivities = goalDao.getGoalAndRelatedActivities(goal.uid).asLiveData()

    fun save() {
        if (isNew) {
            isNew = false
            viewModelScope.launch {
                goal.uid = goalDao.insert(goal)
            }
        } else {
            viewModelScope.launch {
                goalDao.update(goal)
            }
        }
        viewModelScope.launch {
            var hundredPercentValue = HundredPercentValue()
            hundredPercentValueDao.update(hundredPercentValue.date)
        }
    }

    fun delete() = viewModelScope.launch {
        goalDao.deleteGoalAndRelatedChallenges(goal)
        goal = Goal()
        isNew = true
    }
}
