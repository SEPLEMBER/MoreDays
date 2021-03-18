package de.wuapps.moredays.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.HundredPercentValueDao
import de.wuapps.moredays.database.dao.TrophyDao

class GoalsChartsViewModelFactory(
    private val goalDao: GoalDao,
    private val entryDao: ActivityEntryDao,
    private val trophyDao: TrophyDao,
    private val hundredPercentValueDao: HundredPercentValueDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalsChartsViewModel(goalDao, entryDao, trophyDao, hundredPercentValueDao) as T
    }
}