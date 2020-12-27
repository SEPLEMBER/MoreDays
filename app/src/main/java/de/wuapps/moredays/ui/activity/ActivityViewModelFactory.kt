package de.wuapps.moredays.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal


class ActivityViewModelFactory
    (private val activityRepository: ActivityRepository, private val goal: Goal, private val activity: Activity?) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
                return ActivityViewModel(activityRepository, goal, activity) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
