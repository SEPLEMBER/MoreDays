package de.wuapps.moredays.ui.home

import android.util.Log
import androidx.lifecycle.*
import de.wuapps.moredays.database.Analysis
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(
    goalDao: GoalDao,
    scaleDao: ScaleDao,
    private val challengeDao: ChallengeDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao,
    private val trophyDao: TrophyDao
) : ViewModel() {

    val hasFab = true
    val trophies = MutableLiveData<MutableList<Trophy>>()
    val justReached100Percent = MutableLiveData<Boolean>()
    val percent = MutableLiveData<Float>()
    val date: Calendar = Calendar.getInstance()
    val scales = scaleDao.getAll().asLiveData().map { scale -> scale}
    val goalEntries = goalDao.getAllDataForGivenDate().asLiveData()
        .map { goalData -> mapGoalDataToGoalEntries(goalData) }
    private var was100Percent = false //before an activity is inserted this is set to true/false, hence with an update of goalData reached100Percent may be set accordingly
    private val analysis = Analysis()
    private var latestActivity: Activity? = null

    init {
        Log.w("homeviewmodel", "init homeviewmodel")
        trophies.value = ArrayList<Trophy>()
        justReached100Percent.value = false
        percent.value = 0F
    }

    private fun mapGoalDataToGoalEntries(goalData: List<GoalAndRelatedData>): List<GoalEntry> {
        val result = mutableListOf<GoalEntry>()
        for (item in goalData) {
            result += GoalEntry(
                item,
                date
            )
        }
        calcPercentage(result)
        return result
    }

    private suspend fun analyzeGoalTrophies(goal: Goal, activity: Activity) {
        val newTrophies = analysis.checkForNewTrophies(goal, activity.uid, date, trophyDao, activityEntryDao)
        if (newTrophies.size > 0) {
            trophies.value!!.clear()
            trophies.postValue(newTrophies as MutableList<Trophy>)
        }
    }

    private fun analyzeScaleTrophies(scaleId: Long) {

    }

    fun addGoalEntry(goal: Goal, activity: Activity) {
        val id = -1L
        was100Percent = (percent.value!! >= 100F)
        viewModelScope.launch(Dispatchers.IO) {
            val id = activityEntryDao.insert(
                ActivityEntry(activity)
            )
            activity.uid = id
            analyzeGoalTrophies(goal, activity)
        }
        latestActivity = activity
    }

    fun addScaleEntry(scaleId: Long, value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            scaleEntryDao.insert(
                ScaleEntry(scaleId, value)
            )
        }
        analyzeScaleTrophies(scaleId)
    }

    private fun calcPercentage(goalEntries: List<GoalEntry>) {
        val sum = goalEntries.sumBy { goalEntry -> goalEntry.getCurrentValue() }
        val desired = goalEntries.sumBy { goalEntry -> goalEntry.getDesiredValue() }
        if (sum > 0 && desired > 0) {
            if (sum >= desired && !was100Percent && latestActivity!=null)
                justReached100Percent.value = true
            percent.value = sum * 100F / desired
        }
    }
}

