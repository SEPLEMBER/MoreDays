package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.GoalAndRelatedActivities
import de.wuapps.moredays.database.entity.GoalAndRelatedData
import de.wuapps.moredays.database.entity.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal order by order_number asc")
    fun getAll(): Flow<List<Goal>>

    @Query("SELECT color FROM goal order by order_number asc")
    fun getAllColors(): Flow<List<Int>>

    @Transaction
    @Query("SELECT * FROM goal where uid = :goalId")
    fun getGoalAndRelatedActivities(goalId: Long): Flow<GoalAndRelatedActivities?>

    @Transaction
    @Query("SELECT * FROM goal where goal.is_old = 0 order by order_number asc")
    fun getAllDataForGivenDate(): Flow<List<GoalAndRelatedData>>

    //  @Transaction
//  @Query("SELECT * FROM goal LEFT JOIN activity INNER JOIN activityentry ON goal.uid = activity.goal_id and activityentry.goal_id = goal.uid and activityentry.activity_id = activity.uid")
    // fun getAllDataForGivenDate():Flow<List<GoalAndRelatedData>>
    @get:Query("SELECT count(*) FROM goal")
    val countGoals: Int

    @get:Query("SELECT * FROM goal where is_active>0 order by order_number asc")
    val activeCategories: List<Goal>

    @get:Query("SELECT sum(points) FROM goal where is_active> 0")
    val sumPointsOfActive: Int

    @Query("SELECT * FROM goal WHERE uid = :goalId")
    suspend fun getGoalById(goalId: Long): Goal? //one shot read is the one we need -- we are the only ones, who update the data

    @Query("SELECT * FROM goal WHERE name = :name")
    suspend fun getGoalByName(name: String): Goal?

    @Query("SELECT count(*) from Activity where activity.goal_id =:goalId")
    suspend fun isUsed(goalId: Long): Int

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
}