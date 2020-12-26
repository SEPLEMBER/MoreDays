package de.wuapps.moredays.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository


class ActivityViewModelFactory
    (private val activityRepository: ActivityRepository, private val goalRepository: GoalRepository, private val activityId: Long) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
                return ActivityViewModel(activityRepository, goalRepository, activityId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
