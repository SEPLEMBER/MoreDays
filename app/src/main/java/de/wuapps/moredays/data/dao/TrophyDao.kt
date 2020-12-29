package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Trophy
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TrophyDao {
  @Query("SELECT * FROM Trophy order by timestamp desc")
  fun getAll(): Flow<List<Trophy>>

  @Query("SELECT count(*) FROM trophy ")
  fun getCountTrophies(): Int

  @Query("SELECT * FROM Trophy where date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
  fun getAllForToday(): List<Trophy?>?

  @Query("SELECT * FROM Trophy where strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
  fun getAllForThisWeek(): List<Trophy?>?

  @Query("SELECT * FROM Trophy where goal_id = :goalId order by timestamp desc")
  fun getByGoalId(goalId: Int): List<Trophy?>?

  @Query("SELECT * FROM Trophy where goal_id = :goalId and strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
  fun getByGoalIdForThisWeek(goalId: Int): List<Trophy?>?

  @Query("SELECT * FROM Trophy where goal_id = :goalId and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
  fun getByGoalIdForToday(goalId: Int): List<Trophy?>?

  @Query("SELECT count(*) from trophy where goal_id = :goalId and trophy.type = :type and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
  fun getCountByGoalIdAndTypeAndTimespan(
    goalId: Int,
    type: Int,
    from: Calendar?,
    to: Calendar?
  ): Int

  @Query("SELECT count(*) from trophy where goal_id = :goalId and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
  fun getCountByGoalIdAndTimespan(goalId: Int, from: Calendar?, to: Calendar?): Int

  @Query("SELECT count(*) from trophy where activity_entry_id = :activityEntryId")
  fun getCountByEntryId(activityEntryId: Int): Int

  @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
  fun getCountToGoalAndType(goalId: Int, type: Int): Int

  @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(:since / 1000 , 'unixepoch')) <= date(datetime(trophy.timestamp / 1000 , 'unixepoch')) ")
  fun getCountToGoalAndTypeSince(goalId: Int, type: Int, since: Calendar?): Int

  @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
  fun getCountToGoalAndTypeForGivenDate(goalId: Int, type: Int, date: Calendar?): Int


  @Query("SELECT max(trophy.value) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
  fun getMaxValueToGoalAndType(goalId: Int, type: Int): Int

  @Query("SELECT count(*) FROM trophy where type= :trophyType and goal_id=:goalId and datetime(timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
  fun getCountToTimeSpan(goalId: Int, trophyType: Int, from: Calendar?, to: Calendar?): Int

  @Insert(onConflict = REPLACE)
  fun insert(item: Trophy?): Long

  @Insert
  fun insertData(data: List<Trophy?>?)

  @Query("Delete from Trophy")
  fun deleteAll()

  @Query("Delete from trophy where trophy.uid = :id")
  fun delete(id: Int)

  @Delete
  fun delete(item: Trophy?)

  @Update
  fun update(item: Trophy?)

}
