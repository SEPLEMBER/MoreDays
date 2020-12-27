package de.wuapps.moredays.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import java.util.*

class SharedViewModel(
    activityRepository: ActivityRepository,
    goalRepository: GoalRepository,
    challengeRepository: ChallengeRepository
) : BaseViewModel(){
    val goals = goalRepository.allGoals.asLiveData()
    val activities = activityRepository.allActivitys.asLiveData()
    val challenges = challengeRepository.allChallenges.asLiveData()
    val date: LiveData<Date> = liveData {
        Date()
    }
}