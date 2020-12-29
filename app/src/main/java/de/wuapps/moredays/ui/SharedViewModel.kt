package de.wuapps.moredays.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.data.repository.ScaleRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@InternalCoroutinesApi
class SharedViewModel(
    activityRepository: ActivityRepository,
    goalRepository: GoalRepository,
    challengeRepository: ChallengeRepository,
    scaleRepository: ScaleRepository
) : BaseViewModel() {
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

data class AllData(
    var goals: List<Goal>,
    val activities: List<Activity>,
    val scales: List<Scale>,
    val challenges: List<Challenge>
)