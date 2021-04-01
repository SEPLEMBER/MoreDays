package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.GoalAndRelatedActivities
import de.wuapps.moredays.database.entity.GoalAndRelatedData
import kotlinx.coroutines.flow.Flow


@Dao
interface GoalDao {
    @Query("SELECT * FROM goal")
    fun getAll(): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * FROM goal")
    fun getAllWithRelatedActivities(): Flow<List<GoalAndRelatedActivities>>

    @Query("SELECT color FROM goal")
    fun getAllColors(): Flow<List<Int>>

    @Transaction
    @Query("SELECT * FROM goal where uid = :goalId")
    fun getGoalAndRelatedActivities(goalId: Long): Flow<GoalAndRelatedActivities?>

    @Transaction
    @Query("SELECT * FROM goal where goal.is_old = 0")
    fun getAllGoalData(): Flow<List<GoalAndRelatedData>>

    @Query("SELECT count(*) from Activity where activity.goal_id =:goalId")
    suspend fun isUsed(goalId: Long): Int

    @Query("DELETE from Challenge where challenge.challenge_type=2 and challenge.id =:goalId")
    suspend fun deleteRelatedChallenges(goalId: Long): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(goal: Goal): Long

    @Insert
    fun insertData(data: List<Goal>)

    @Query("Delete from goal")
    fun deleteAll()

    @Delete
    suspend fun delete(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Update
    fun update(goals: List<Goal>)

    @Transaction
    suspend fun deleteGoalAndRelatedChallenges(goal:Goal) {
        delete(goal)
        deleteRelatedChallenges(goal.uid)
    }
}