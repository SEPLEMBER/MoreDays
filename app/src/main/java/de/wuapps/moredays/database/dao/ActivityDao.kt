package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.wuapps.moredays.database.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT activity.uid, activity.goal_id, activity.points, activity.name, activity.is_old FROM Activity order by goal_id asc, activity.points desc")
    fun getAll(): Flow<List<Activity>>

    @Query("SELECT * FROM Activity WHERE uid = :id")
    suspend fun getActivityById(id: Long): Activity?

    @Query("Select count(*) from Activity where activity.uid = :activityId")
    suspend fun isUsed(activityId: Long): Int

    @Insert
    suspend fun insert(activity: Activity): Long

    @Delete
    suspend fun delete(activity: Activity)

    @Query("Delete from Activity")
    suspend fun deleteAll()

    @Update
    suspend fun update(entryType: Activity)

}