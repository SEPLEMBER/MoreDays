package de.wuapps.moredays.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.ScaleDao

class HomeViewModelFactory
    (private val goalDao: GoalDao, private val scaleDao: ScaleDao, private val challengeDao: ChallengeDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(goalDao, scaleDao, challengeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}