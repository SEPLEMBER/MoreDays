package de.wuapps.moredays.ui.tools.imexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.*

class ImportExportViewModelFactory(
    private val goalDao: GoalDao,
    private val activityDao: ActivityDao,
    private val scaleDao: ScaleDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao,
    private val journalDao: JournalDao,
    private val trophyDao: TrophyDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImportExportViewModel(
            goalDao,
            activityDao,
            scaleDao,
            activityEntryDao,
            scaleEntryDao,
            journalDao,
            trophyDao
        ) as T
    }
}
