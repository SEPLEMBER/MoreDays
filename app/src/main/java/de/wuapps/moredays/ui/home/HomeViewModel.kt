package de.wuapps.moredays.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.Analysis
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.Dispatchers
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
    var name = ""
    val newTrophies = MutableLiveData<MutableList<Trophy>>()
    val justReached100Percent = MutableLiveData<Boolean>()
    val justMadeChallenge = MutableLiveData<Boolean>()
    var madeChallenge: Challenge? = null
    val percent = MutableLiveData<Float>()
    var date: Calendar = Calendar.getInstance()
    private val _scales = scaleDao.getAll()
    val scales = _scales.asLiveData()
    private val _challenges = challengeDao.getAllDue()
    val challenges = _challenges.asLiveData()
    private val _goalData = goalDao.getAllGoalData()
    var timeLatestActive: Calendar? = Calendar.getInstance()
    val goalEntryList = MutableLiveData<MutableList<GoalEntry>>()
    private var was100Percent =
        false //before an activity is inserted this is set to true/false, hence with an update of goalData reached100Percent may be set accordingly
    private val analysis = Analysis()
    private var latestActivity: Activity? = null
    var latestGoal: Goal? = null
    private val _activityEntries = activityEntryDao.getAll()
    private val _scaleEntries = scaleEntryDao.getAll()
    private val _trophies = trophyDao.getAll()
    private val challengesList = ArrayList<Challenge>()
    private val activityEntriesList = ArrayList<ActivityEntry>()
    private val scaleList = ArrayList<Scale>()
    private val scaleEntriesList = ArrayList<ScaleEntry>()
    private val trophyList = ArrayList<Trophy>()

    init {
        newTrophies.value = ArrayList<Trophy>()
        justReached100Percent.value = false
        justMadeChallenge.value = false
        percent.value = 0F
        //goalEntryList.value = ArrayList<GoalEntry>()
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
            checkChallenges()
        }.launchIn(viewModelScope)
        _challenges.onEach {
            challengesList.clear()
            challengesList.addAll(it)
        }.launchIn(viewModelScope)
        _scales.onEach {
            scaleList.clear()
            scaleList.addAll(it)
        }.launchIn(viewModelScope)
        _goalData.onEach {
            mapGoalDataToGoalEntries(it)
        }.launchIn(viewModelScope)
    }

    private fun mapGoalDataToGoalEntries(goalData: List<GoalAndRelatedData>) {
        val result = mutableListOf<GoalEntry>()
        for (item in goalData) {
            result.add(GoalEntry(item, date))
        }
        calcPercentage(result)
        goalEntryList.postValue(result)
    }

    private suspend fun analyzeGoalTrophies(goal: Goal, entryId: Long) {
        latestGoal = goal
        val newTrophies =
            analysis.checkForNewTrophies(goal, entryId, date, trophyList, activityEntriesList)
        if (newTrophies.isNotEmpty()) {
            this.newTrophies.value!!.clear()
            this.newTrophies.postValue(newTrophies as MutableList<Trophy>)
        }
        viewModelScope.launch(Dispatchers.IO) { trophyDao.insert(newTrophies) }
    }

    fun addGoalEntry(goal: Goal, activity: Activity) {
        was100Percent = (percent.value!! >= 100F)
        viewModelScope.launch(Dispatchers.IO) {
            val activityEntry = ActivityEntry(activity)
            activityEntry.timestamp = date
            val id = activityEntryDao.insert(activityEntry)
            activityEntry.uid = id
            activityEntriesList.add(activityEntry) //später besser mit observe arbeiten!
            checkChallenges()
            analyzeGoalTrophies(goal, id)
        }
        latestActivity = activity
    }

    fun addScaleEntry(scaleId: Long, value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val scaleEntry = ScaleEntry(scaleId, value)
            scaleEntry.timestamp = date
            val id = scaleEntryDao.insert(scaleEntry)
            scaleEntry.uid = id
            scaleEntriesList.add(scaleEntry)
            checkChallenges()
        }
    }

    private fun checkChallenges() {
        viewModelScope.launch(Dispatchers.IO) {
            val changedChallenges = analysis.checkChallenges(
                challengesList,
                activityEntriesList,
                scaleEntriesList,
                trophyList
            )
            if (changedChallenges.first.isNotEmpty())
                challengeDao.update(changedChallenges.first)
            if (changedChallenges.second.isNotEmpty()){
                madeChallenge = changedChallenges.second.last()
                justMadeChallenge.postValue(true)
            }
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

    fun changeDate(calendarDate: Calendar) {
        date = calendarDate
        if (goalEntryList.value == null)
            return
        val tmp = ArrayList<GoalEntry>()
        for (item in goalEntryList.value!!) {
            tmp.add(item.getCopy(calendarDate))
        }
        goalEntryList.postValue(tmp)
        calcPercentage(tmp)
    }

    fun getLatestValueToScale(scale: Scale): Float {
        return scaleEntriesList.filter { it.scaleId == scale.uid }.lastOrNull()?.value
            ?: (scale.maxValue - scale.minValue) / 2.0F
    }
}

