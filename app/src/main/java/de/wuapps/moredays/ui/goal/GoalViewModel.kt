package de.wuapps.moredays.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.entity.GoalAndRelatedActivities
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.HundredPercentValueDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.HundredPercentValue
import kotlinx.coroutines.launch
import java.util.*

class GoalViewModel(private val goalDao: GoalDao, private val hundredPercentValueDao: HundredPercentValueDao,  _goal: Goal?) :
    ViewModel() {
    var isNew = _goal == null
    var goal: Goal
    var isUsed = false
    val hasFab = false
    lateinit var goalAndRelatedActivities: LiveData<GoalAndRelatedActivities?>

    init {
        goal = if (isNew) {
            Goal()
        } else {
            _goal!!
        }
        viewModelScope.launch {
            goalAndRelatedActivities = goalDao.getGoalAndRelatedActivities(goal.uid).asLiveData()
            if (goalAndRelatedActivities.value != null)
                isUsed == goalAndRelatedActivities.value!!.activities.isNotEmpty()
        }
    }

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
        goalDao.delete(goal)
        goal = Goal()
        isNew = true
    }
}
