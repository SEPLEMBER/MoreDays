package de.wuapps.moredays.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.ui.activity.ActivityViewModel

class SharedViewModelFactory
    (private val activityRepository: ActivityRepository, private val goalRepository: GoalRepository, private val challengeRepository: ChallengeRepository, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(activityRepository, goalRepository, challengeRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}