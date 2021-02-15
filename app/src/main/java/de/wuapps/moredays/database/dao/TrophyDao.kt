package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.database.entity.Trophy
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TrophyDao {
    @Query("SELECT * FROM Trophy order by timestamp desc")
    fun getAll(): Flow<List<Trophy>>

    @Query("SELECT * FROM Trophy order by timestamp desc")
    fun getAllWithGoal(): Flow<List<TrophyAndRelatedGoal>>

    @Query("SELECT * FROM Trophy order by timestamp desc")
    suspend fun getAllOnce(): List<Trophy>

    @Query("SELECT count(*) FROM trophy ")
    suspend fun getCountTrophies(): Int

    @Query("SELECT * FROM Trophy where date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
    suspend fun getAllForToday(): List<Trophy>

    @Query("SELECT * FROM Trophy where strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
    suspend fun getAllForThisWeek(): List<Trophy>

    @Query("SELECT * FROM Trophy where goal_id = :goalId order by timestamp desc")
    suspend fun getByGoalId(goalId: Long): List<Trophy>

    @Query("SELECT * FROM Trophy where goal_id = :goalId and strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
    suspend fun getByGoalIdForThisWeek(goalId: Long): List<Trophy>

    @Query("SELECT * FROM Trophy where goal_id = :goalId and type = :type and strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
    suspend fun getByGoalIdAndTypeForThisWeek(goalId: Long, type: Int): List<Trophy>

    @Query("SELECT * FROM Trophy where goal_id = :goalId and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
    suspend fun getByGoalIdForToday(goalId: Long): List<Trophy>

    @Query("SELECT count(*) from trophy where goal_id = :goalId and trophy.type = :type and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    suspend fun getCountByGoalIdAndTypeAndTimespan(
        goalId: Long,
        type: Int,
        from: Calendar,
        to: Calendar
    ): Int

    @Query("SELECT count(*) from trophy where goal_id = :goalId and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    suspend fun getCountByGoalIdAndTimespan(goalId: Long, from: Calendar, to: Calendar): Int

    @Query("SELECT count(*) from trophy where activity_entry_id = :activityEntryId")
    suspend fun getCountByEntryId(activityEntryId: Long): Int

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
    suspend fun getCountToGoalAndType(goalId: Long, type: Int): Int

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(:since / 1000 , 'unixepoch')) <= date(datetime(trophy.timestamp / 1000 , 'unixepoch')) ")
    suspend fun getCountToGoalAndTypeSince(goalId: Long, type: Int, since: Calendar): Int

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    suspend fun getCountToGoalAndTypeForGivenDate(goalId: Long, type: Int, date: Calendar): Int


    @Query("SELECT max(value) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
    suspend fun getMaxValueToGoalAndType(goalId: Long, type: Int): Int?

    @Query("SELECT count(*) FROM trophy where type= :trophyType and goal_id=:goalId and datetime(timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    suspend fun getCountToTimeSpan(goalId: Long, trophyType: Int, from: Calendar, to: Calendar): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(item: Trophy): Long

    @Insert
    suspend fun insertData(data: List<Trophy>)

    @Query("Delete from Trophy")
    suspend fun deleteAll()

    @Query("Delete from trophy where trophy.uid = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(item: Trophy)

    @Update
    suspend fun update(item: Trophy)

}
