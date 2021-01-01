package de.wuapps.moredays.ui.home

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.Scale
import java.util.*

class HomeViewModel(
    goalDao: GoalDao,
    scaleDao: ScaleDao,
    private val challengeDao: ChallengeDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao
) : ViewModel() {

    private val scalesGoals = Pair<MutableList<EntryType>, MutableList<EntryType>>(mutableListOf(), mutableListOf()) //scales, goalData
    val date = Calendar.getInstance()
    private val scales =
        scaleDao.getAll().asLiveData().map { scales -> mapScalesToEntryTypes(scales) }
    private val goalData = goalDao.getAllDataForGivenDate().asLiveData()
        .map { goalData -> mapGoalDataToEntryTypes(goalData) }
    val entryTypes = MediatorLiveData<List<EntryType>>()

    private fun mapScalesToEntryTypes(scales: List<Scale>): List<EntryType> {
        val result = mutableListOf<EntryType>()
        for (scale in scales) {
            result += ScaleEntry(
                scale,
                scaleEntryDao
            )
        }
        return result
    }

    private fun mapGoalDataToEntryTypes(goalData: List<GoalAndRelatedData>): List<EntryType> {
        val result = mutableListOf<EntryType>()
        for (item in goalData) {
            result += GoalEntry(
                item,
                date,
                activityEntryDao
            )
        }
        return result

    }

    init {
        entryTypes.addSource(scales)
        {
            if (it != null) {
                updateScales(it)
            }
        }
        entryTypes.addSource(goalData)
        {
            if (it != null) {
                updateGoalData(it)
            }
        }

    }

    private fun updateScales(scales: List<EntryType>){
        scalesGoals.first.clear()
        scalesGoals.first.addAll(scales)
        entryTypes.value = scalesGoals.second + scalesGoals.first
    }
    private fun updateGoalData(goalData: List<EntryType>){
        scalesGoals.second.clear()
        scalesGoals.second.addAll(goalData)
        entryTypes.value = scalesGoals.second + scalesGoals.first
    }
}

