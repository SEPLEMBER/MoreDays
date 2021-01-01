package de.wuapps.moredays.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.*

class HomeViewModelFactory
    (private val goalDao: GoalDao,private val  scaleDao: ScaleDao, private val challengeDao: ChallengeDao, private val activityEntryDao: ActivityEntryDao, private val scaleEntryDao: ScaleEntryDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(goalDao, scaleDao, challengeDao, activityEntryDao, scaleEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}