package de.wuapps.moredays.ui.charts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.TrophyDao
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GoalsChartsViewModel(
    goalDao: GoalDao,
    entryDao: ActivityEntryDao,
    trophyDao: TrophyDao
) : ViewModel() {
    private val goalsFlow = goalDao.getAll()
    private val goals = ArrayList<Goal>()
    private val activityEntriesFlow = entryDao.getAllAndRelatedData()
    private val activityEntries = ArrayList<ActivityEntryAndRelatedData>()
    private val trophiesFlow = trophyDao.getAll()
    private val trophies = ArrayList<Trophy>()
    val enoughData = MutableLiveData<Boolean>()
    val goalSummaries = MutableLiveData<MutableList<GoalSummary>>()

    init {
        enoughData.value = true
        goalSummaries.value = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            observeData()

        }

    }

    private fun observeData() {
        activityEntriesFlow.onEach {
            activityEntries.clear()
            activityEntries.addAll(it)
            onUpdate()
        }.launchIn(viewModelScope)
        goalsFlow.onEach {
            goals.clear()
            goals.addAll(it)
            onUpdate()
        }.launchIn(viewModelScope)
        trophiesFlow.onEach {
            trophies.clear()
            trophies.addAll(it)
            onUpdate()
        }.launchIn(viewModelScope)
    }

    private fun onUpdate(){
        val tmp = ArrayList<GoalSummary>()
        if (activityEntries.size == 0 || goals.size == 0 || trophies.size == 0) return //not much to show
        for (goal in goals){
            var trophiesOfThisGoal = trophies.filter { trophy -> trophy.goalId == goal.uid }
            var activityEntriesOfThisGoal = activityEntries.filter { activityEntry -> activityEntry.goal.uid == goal.uid }
            var sumPoints = activityEntriesOfThisGoal.sumBy { activityEntry -> activityEntry.activity.points }
            var countDaysMade = trophiesOfThisGoal.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL }.size
            var countDaysInRow = trophiesOfThisGoal.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW}.sortedByDescending { trophy -> trophy.timestamp}.firstOrNull()?.value ?: 0
            var activitySummaries = ArrayList<ActivitySummary>()
            for (entry in activityEntriesOfThisGoal.groupBy { it.activity }){
                activitySummaries.add(ActivitySummary(entry.key, entry.value.size))
            }
            tmp.add( GoalSummary(goal, sumPoints, countDaysMade, countDaysInRow, activitySummaries))
        }
        goalSummaries.postValue(tmp)
        enoughData.value = true
    }
    data class ActivitySummary(val activity: Activity, val countMade: Int)
    data class GoalSummary(
        val goal: Goal,
        val sumPoints: Int,
        val countDaysMade: Int,
        val countDaysInRow: Int,
        val activitySummaries: MutableList<ActivitySummary>
    )
}