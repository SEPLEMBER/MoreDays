package de.wuapps.moredays.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.GoalAndRelatedActivities
import kotlinx.coroutines.launch

class ChallengeViewModel(private val challengeDao: ChallengeDao, private val goalDao: GoalDao, private val activityDao: ActivityDao, val scaleDao: ScaleDao, _challenge: Challenge?) : ViewModel() {
    var isNew = _challenge == null
    var challenge: Challenge
    var goals = goalDao.getAll().asLiveData()
    var scales = scaleDao.getAll().asLiveData()
    var activities = activityDao.getAll().asLiveData()

    var isUsed = false
    val hasFab = false

    init {
        challenge = if (isNew) {
            Challenge()
        } else {
            _challenge!!
        }
    }

    fun save() {
        if (isNew) {
            isNew = false
            viewModelScope.launch {
                challenge.uid = challengeDao.insert(challenge)
            }
        } else {
            viewModelScope.launch {
                challengeDao.update(challenge)
            }
        }
    }

    fun delete() = viewModelScope.launch {
        challengeDao.delete(challenge)
        challenge = Challenge()
        isNew = true
    }
}
