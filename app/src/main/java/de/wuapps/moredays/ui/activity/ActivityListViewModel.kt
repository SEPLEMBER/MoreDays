package de.wuapps.moredays.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.ui.BaseViewModel

class ActivityListViewModel(private val activityRepository: ActivityRepository
) : BaseViewModel() {
    val activities: LiveData<List<Activity>> = activityRepository.allActivitys.asLiveData()
}