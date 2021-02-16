package de.wuapps.moredays.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Challenge

class ChallengeViewModelFactory(private val challengeDao: ChallengeDao, private val goalDao: GoalDao, private val activityDao: ActivityDao, val scaleDao: ScaleDao, private val challenge: Challenge?) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChallengeViewModel(challengeDao, goalDao, activityDao, scaleDao, challenge) as T
    }
}
