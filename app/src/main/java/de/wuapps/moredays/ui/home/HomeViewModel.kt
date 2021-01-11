package de.wuapps.moredays.ui.home

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

    val scalesGoals = Pair<MutableList<ScaleEntry>, MutableList<GoalEntry>>(
        mutableListOf(),
        mutableListOf()
    ) //scales, goalData
    val date: Calendar = Calendar.getInstance()
    private val scales =
        scaleDao.getAll().asLiveData().map { scales -> mapScalesToScaleEntries(scales) }
    private val goalData = goalDao.getAllDataForGivenDate().asLiveData()
        .map { goalData -> mapGoalDataToGoalEntries(goalData) }
    val entryTypes = MediatorLiveData<List<EntryType>>()

    private fun mapScalesToScaleEntries(scales: List<Scale>): List<ScaleEntry> {
        val result = mutableListOf<ScaleEntry>()
        for (scale in scales) {
            result += ScaleEntry(
                scale,
                scaleEntryDao
            )
        }
        return result
    }

    private fun mapGoalDataToGoalEntries(goalData: List<GoalAndRelatedData>): List<GoalEntry> {
        val result = mutableListOf<GoalEntry>()
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

    private fun updateScales(scales: List<ScaleEntry>) {
        scalesGoals.first.clear()
        scalesGoals.first.addAll(scales)
        entryTypes.value = scalesGoals.second + scalesGoals.first
    }

    private fun updateGoalData(goalData: List<GoalEntry>) {
        scalesGoals.second.clear()
        scalesGoals.second.addAll(goalData)
        entryTypes.value = scalesGoals.second + scalesGoals.first
    }
}

