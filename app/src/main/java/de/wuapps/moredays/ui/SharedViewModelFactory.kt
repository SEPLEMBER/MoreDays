package de.wuapps.moredays.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.data.repository.ScaleRepository
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.ui.activity.ActivityViewModel
import kotlinx.coroutines.InternalCoroutinesApi

class SharedViewModelFactory
    (private val activityRepository: ActivityRepository, private val goalRepository: GoalRepository, private val challengeRepository: ChallengeRepository, private val scaleRepository: ScaleRepository) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(activityRepository, goalRepository, challengeRepository, scaleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}