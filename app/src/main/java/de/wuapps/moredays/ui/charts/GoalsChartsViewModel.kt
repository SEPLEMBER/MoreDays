package de.wuapps.moredays.ui.charts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.HundredPercentValueDao
import de.wuapps.moredays.database.dao.TrophyDao
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class GoalsChartsViewModel(
    goalDao: GoalDao,
    entryDao: ActivityEntryDao,
    trophyDao: TrophyDao,
    hundredPercentValueDao: HundredPercentValueDao
) : ViewModel() {
    private val goalsFlow = goalDao.getAll()
    private val goals = ArrayList<Goal>()
    private val activityEntriesFlow = entryDao.getAllAndRelatedData()
    private val activityEntries = ArrayList<ActivityEntryAndRelatedData>()
    private val trophiesFlow = trophyDao.getAll()
    private val trophies = ArrayList<Trophy>()
    private val hundredPercentValueFlow = hundredPercentValueDao.getAll()
    private val hundredPercentValue = ArrayList<HundredPercentValue>()
    val goalSummaries = MutableLiveData<MutableList<GoalSummary>>()
    val goalHistory = MutableLiveData<MutableList<GoalsDate>>()

    init {
        goalSummaries.value = ArrayList()
        goalHistory.value = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            observeData()
        }

    }

    private fun observeData() {
        activityEntriesFlow.onEach {
            activityEntries.clear()
            activityEntries.addAll(it)
            onUpdate()
            onUpdateHistory()
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
        hundredPercentValueFlow.onEach {
            hundredPercentValue.clear()
            hundredPercentValue.addAll(it)
            onUpdateHistory()
        }.launchIn(viewModelScope)
    }

    private fun onUpdate() {
        val tmpSummary = ArrayList<GoalSummary>()
        if (activityEntries.size == 0 || goals.size == 0 || trophies.size == 0 ) return //not much to show
        var daysTotal = activityEntries.groupBy { Pair(it.activityEntry.timestamp.get(Calendar.YEAR), it.activityEntry.timestamp.get(Calendar.DAY_OF_YEAR)) }.size
        for (goal in goals) {
            var trophiesOfThisGoal = trophies.filter { trophy -> trophy.goalId == goal.uid }
            var activityEntriesOfThisGoal =
                activityEntries.filter { activityEntry -> activityEntry.goal.uid == goal.uid }
            var sumPoints =
                activityEntriesOfThisGoal.sumBy { activityEntry -> activityEntry.activity.points }
            var countDaysMade =
                trophiesOfThisGoal.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL }.size
            var countDaysInRow =
                trophiesOfThisGoal.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW }
                    .sortedByDescending { trophy -> trophy.timestamp }.firstOrNull()?.value ?: 0
            var activitySummaries = ArrayList<ActivitySummary>()
            for (entry in activityEntriesOfThisGoal.groupBy { it.activity }) {
                activitySummaries.add(ActivitySummary(entry.key, entry.value.size))
            }
            tmpSummary.add(
                GoalSummary(
                    goal,
                    sumPoints,
                    daysTotal,
                    countDaysMade,
                    countDaysInRow,
                    activitySummaries
                )
            )
        }
        goalSummaries.postValue(tmpSummary)
    }
    private fun onUpdateHistory(){
        if (activityEntries.size == 0 || goals.size == 0 || hundredPercentValue.size == 0) return
        goals.sortedBy { it.uid }
        val currentHundredPercentValue = goals.sumBy { it.points }
        var tmpHistory = ArrayList<GoalsDate>()
        for (day in activityEntries.groupBy { Pair(it.activityEntry.timestamp.get(Calendar.YEAR), it.activityEntry.timestamp.get(Calendar.DAY_OF_YEAR)) }){ //ignore days without entries!
            //select latest from 100% < date -->dao
            val goalSumsDate = ArrayList<Pair<Goal, Int>>()
            for(goal in goals){
                goalSumsDate.add(Pair(goal, day.value.filter{it.goal.uid == goal.uid}.sumBy { it.activity.points }))
            }
            val hundredPercent = hundredPercentValue.filter { it.getDateAsCalendar().get(Calendar.YEAR) == day.key.first &&  it.getDateAsCalendar().get(Calendar.DAY_OF_YEAR) <= day.key.second}.firstOrNull()?.points
                ?: currentHundredPercentValue
            val calendarDay = Calendar.getInstance()
            calendarDay.set(Calendar.YEAR, day.key.first)
            calendarDay.set(Calendar.DAY_OF_YEAR, day.key.second)
            tmpHistory.add(GoalsDate(calendarDay, hundredPercent, goalSumsDate))
        }
        goalHistory.postValue(tmpHistory)
    }
    data class ActivitySummary(val activity: Activity, val countMade: Int)
    data class GoalsDate(val day: Calendar, val hundredPercent: Int, val goalPoints: MutableList<Pair<Goal, Int>>)
    data class GoalSummary(
        val goal: Goal,
        val sumPoints: Int,
        val daysTotal: Int,
        val countDaysMade: Int,
        val countDaysInRow: Int,
        val activitySummaries: MutableList<ActivitySummary>
    )
}