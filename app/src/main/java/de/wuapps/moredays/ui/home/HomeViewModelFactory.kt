package de.wuapps.moredays.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.*

class HomeViewModelFactory
    (
    private val goalDao: GoalDao,
    private val scaleDao: ScaleDao,
    private val activityEntryDao: ActivityEntryDao,
    private val scaleEntryDao: ScaleEntryDao,
    private val trophyDao: TrophyDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
                goalDao,
                scaleDao,
                activityEntryDao,
                scaleEntryDao,
                trophyDao
            ) as T
    }
}