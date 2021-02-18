package de.wuapps.moredays.ui.home

import android.util.Log
import androidx.lifecycle.*
import de.wuapps.moredays.database.Analysis
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val newTrophies = MutableLiveData<MutableList<Trophy>>()
    val justReached100Percent = MutableLiveData<Boolean>()
    val percent = MutableLiveData<Float>()
    val date: Calendar = Calendar.getInstance()
    private val _scales = scaleDao.getAll()
    val scales = _scales.asLiveData()
    private val _challenges = challengeDao.getAll()
    val challenges = _challenges.asLiveData()
    val goalEntries = goalDao.getAllDataForGivenDate().asLiveData()
        .map { goalData -> mapGoalDataToGoalEntries(goalData) }
    private var was100Percent =
        false //before an activity is inserted this is set to true/false, hence with an update of goalData reached100Percent may be set accordingly
    private val analysis = Analysis()
    private var latestActivity: Activity? = null
    private val _activityEntries = activityEntryDao.getAll()
    private val _scaleEntries = scaleEntryDao.getAll()
    private val _trophies = trophyDao.getAll()
    private val challengesList = ArrayList<Challenge>()
    private val activityEntriesList = ArrayList<ActivityEntry>()
    private val scaleList = ArrayList<Scale>()
    private val scaleEntriesList = ArrayList<ScaleEntry>()
    private val trophyList = ArrayList<Trophy>()

    init {
        Log.w("trapp", "init homeviewmodel")
        newTrophies.value = ArrayList<Trophy>()
        justReached100Percent.value = false
        percent.value = 0F
        viewModelScope.launch(Dispatchers.IO) {
            observeData()
            checkChallenges()
        }

    }

    private fun observeData() {
        _activityEntries.onEach {
            activityEntriesList.clear()
            activityEntriesList.addAll(it)
        }.launchIn(viewModelScope)
        _scaleEntries.onEach {
            scaleEntriesList.clear()
            scaleEntriesList.addAll(it)
        }.launchIn(viewModelScope)
        _trophies.onEach {
            trophyList.clear()
            trophyList.addAll(it)
        }.launchIn(viewModelScope)
        _challenges.onEach {
            challengesList.clear()
            challengesList.addAll(it)
        }.launchIn(viewModelScope)
        _scales.onEach {
            scaleList.clear()
            scaleList.addAll(it)
        }.launchIn(viewModelScope)
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
        val newTrophies =
            analysis.checkForNewTrophies(goal, activity.uid, date, trophyList, activityEntriesList)
        viewModelScope.launch(Dispatchers.IO) { trophyDao.insert(newTrophies) }
        if (newTrophies.size > 0) {
            this.newTrophies.value!!.clear()
            this.newTrophies.postValue(newTrophies as MutableList<Trophy>)
        }
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
            checkChallenges()
        }
        latestActivity = activity
    }

    fun addScaleEntry(scaleId: Long, value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            scaleEntryDao.insert(
                ScaleEntry(scaleId, value)
            )
            checkChallenges()
        }
    }

    private fun checkChallenges() {
        viewModelScope.launch(Dispatchers.IO) {
            val changedChallenges = analysis.checkChallenges(
                challengesList,
                activityEntriesList,
                scaleList,
                scaleEntriesList,
                trophyList
            )
            if (changedChallenges.size > 0)
                challengeDao.update(changedChallenges)
        }
    }

    private fun calcPercentage(goalEntries: List<GoalEntry>) {
        val sum = goalEntries.sumBy { goalEntry -> goalEntry.getCurrentValue() }
        val desired = goalEntries.sumBy { goalEntry -> goalEntry.getDesiredValue() }
        if (sum > 0 && desired > 0) {
            if (sum >= desired && !was100Percent && latestActivity != null)
                justReached100Percent.value = true
            percent.value = sum * 100F / desired
        }
    }
}

