package de.wuapps.moredays.ui.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.HundredPercentValueDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.HundredPercentValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoalViewModel(
    private val goalDao: GoalDao,
    private val hundredPercentValueDao: HundredPercentValueDao,
    _goal: Goal?
) :
    ViewModel() {
    var isNew = _goal == null
    var goal = _goal ?: Goal()
    val hasFab = false
    val goalAndRelatedActivities = goalDao.getGoalAndRelatedActivities(goal.uid).asLiveData()

    fun save() {

        viewModelScope.launch(Dispatchers.IO) {
            val pointsOther = goalDao.getSumOfOtherActive(goal.uid)
            val points = if (goal.isOld) pointsOther else pointsOther + goal.points
            val hundredPercentValue = HundredPercentValue(points)
            hundredPercentValueDao.insert(hundredPercentValue)
            Log.d("oki", "oki1")
            if (isNew) {
                isNew = false
                goal.uid = goalDao.insert(goal)

            } else {
                goalDao.update(goal)
            }
            Log.d("oki", "oki2")


        }
    }

    fun delete() = viewModelScope.launch {
        goalDao.delete(goal)
        goal = Goal()
        isNew = true
    }
}
