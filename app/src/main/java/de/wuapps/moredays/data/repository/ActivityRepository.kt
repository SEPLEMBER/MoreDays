package de.wuapps.moredays.data.repository

import de.wuapps.moredays.database.dao.ActivityDao
import de.wuapps.moredays.database.entity.Activity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allActivitys: Flow<List<Activity>> = activityDao.getAll()

    suspend fun insert(activity: Activity):Long {
        return activityDao.insert(activity)
    }

    suspend fun update(activity: Activity){
        activityDao.update(activity)
    }

    suspend fun delete(activity: Activity) {
        activityDao.delete(activity)
    }

    suspend fun isUsed(activityId: Long):Boolean {
        return activityDao.isUsed(activityId) > 0
    }

    suspend fun getActivityById(id: Long): Activity? {
        return activityDao.getActivityById(id)
    }
}