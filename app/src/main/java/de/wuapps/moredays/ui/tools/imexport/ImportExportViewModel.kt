package de.wuapps.moredays.ui.tools.imexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.*


class ImportExportViewModel(
    private val goalDao: GoalDao,
    private val activityDao: ActivityDao,
    private val scaleDao: ScaleDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao,
    private val journalDao: JournalDao,
    private val trophyDao: TrophyDao
) : ViewModel() {
    val goals = goalDao.getAll().asLiveData()


//    init {
//        goalDao.getAllWithRelatedActivities().onEach { goals.addAll(it) }.launchIn(viewModelScope)
//        activityEntryDao.getAllAndRelatedData().onEach { activityEntries.addAll(it) }
//            .launchIn(viewModelScope)
//        scaleEntryDao.getAllScaleData().onEach { scaleEntries.addAll(it) }.launchIn(viewModelScope)
//        trophyDao.getAllWithGoal().onEach { trophies.addAll(it) }.launchIn(viewModelScope)
//        journalDao.getAll().onEach { journal.addAll(it) }.launchIn(viewModelScope)
//    }
//

}
