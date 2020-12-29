package de.wuapps.moredays.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.data.repository.ScaleRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@InternalCoroutinesApi
class SharedViewModel(
    activityRepository: ActivityRepository,
    goalRepository: GoalRepository,
    challengeRepository: ChallengeRepository,
    scaleRepository: ScaleRepository
) : BaseViewModel() {
    val homeData = goalRepository.allData.asLiveData()
    val _goals = goalRepository.allGoals
    val goals = _goals.asLiveData()
    val _activities = activityRepository.allActivities
    val activities = _activities.asLiveData()
    val _challenges = challengeRepository.allChallenges
    val challenges = _challenges.asLiveData()
    val _scales = scaleRepository.allScales
    val scales = _scales.asLiveData()
    val date: LiveData<Date> = liveData {
        Date()
    }
}




