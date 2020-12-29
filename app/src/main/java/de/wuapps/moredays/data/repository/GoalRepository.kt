package de.wuapps.moredays.data.repository

import de.wuapps.moredays.data.entity.GoalAndRelatedActivities
import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.Goal
import kotlinx.coroutines.flow.Flow
import java.util.*

class GoalRepository(private val goalDao: GoalDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allGoals: Flow<List<Goal>> = goalDao.getAll()
    val allData: Flow<List<GoalAndRelatedData>> = goalDao.getAllDataForGivenDate()

    suspend fun getGoalAndRelatedActivities(id:Long): Flow<GoalAndRelatedActivities?> {
        return goalDao.getGoalAndRelatedActivities(id)
    }




    suspend fun insert(goal: Goal):Long {
        return goalDao.insert(goal)
    }

    suspend fun update(goal: Goal){
        goalDao.update(goal)
    }

    suspend fun delete(goal: Goal) {
        goalDao.delete(goal)
    }

    suspend fun isUsed(goalId: Long):Boolean {
        return goalDao.isUsed(goalId) > 0
    }

    suspend fun getGoalById(id: Long): Goal? {
        return goalDao.getGoalById(id)
    }
    suspend fun getGoalByName(name: String): Goal? {
        return goalDao.getGoalByName(name)
    }
}//goalDao.getGoalById(id) where dao returned flow
    //fun getGoalByName(name: String) = goalDao.getGoalByName(name)
