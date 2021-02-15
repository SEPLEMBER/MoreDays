package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.wuapps.moredays.database.data.SimpleActivity
import de.wuapps.moredays.database.entity.ActivityEntryAndRelatedData
import de.wuapps.moredays.database.entity.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ActivityEntryDao {
    @Query("SELECT * FROM ActivityEntry order by timestamp desc")
    fun getAll(): Flow<List<ActivityEntry>>

    @Query("SELECT * FROM ActivityEntry order by timestamp desc")
    fun getAllAndRelatedData(): Flow<List<ActivityEntryAndRelatedData>>

    @Query("SELECT count(*) FROM ActivityEntry ")
    suspend fun countActivityEntries(): Int

    @Query("SELECT timestamp from ActivityEntry order by timestamp asc limit 1")
    suspend fun  dateOfFirstActivityEntry(): Calendar

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid order by ActivityEntry.timestamp DESC")
    suspend fun  allAsSimple():List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    suspend fun  allAsSimpleForToday():List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    suspend fun  allAsSimpleForThisWeek():List<SimpleActivity>
    @Query("SELECT count(distinct strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')))) FROM ActivityEntry")
    suspend fun  countDaysTracked():Int
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    suspend fun getAllAsSimpleForGivenDate(date:Calendar):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByGoal(goalId:Long):List<SimpleActivity>
    @Query("SELECT count(*) FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    suspend fun getCountByGoal(goalId:Long):Int
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByGoalForToday(goalId:Long):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByGoalForGivenDate(goalId:Long, date:Calendar):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByGoalForThisWeek(goalId:Long):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:activityId and ActivityEntry.uid=Activity.uid order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByActivity(activityId:Long):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:activityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByActivityForToday(activityId:Long):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:activityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByActivityForGivenDate(activityId:Long, date:Calendar):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:activityId and ActivityEntry.uid=Activity.uid and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    suspend fun getAsSimpleByActivityForThisWeek(activityId:Long):List<SimpleActivity>

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.activity_id = Activity.uid where Activity.goal_id = :goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    suspend fun getSumByGoalIdForGivenDate(goalId: Long, date: Calendar): Int

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.activity_id = Activity.uid where date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    suspend fun getSumForGivenDate(date: Calendar): Int

    //@Query("SELECT sum(Activity.points) as sum, strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) as date, Activity.goal_id as goalId FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId group by strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) order by ActivityEntry.timestamp ASC")
    //fun getSumBygoalIdGroupByDay(goalId:Int):List<SumDate>
    @Query("SELECT count(*) FROM ActivityEntry where ActivityEntry.uid = :activityId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    suspend fun getCountToActivityInTimeSpan(activityId: Long, from: Calendar, to: Calendar): Int

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.activity_id = Activity.uid where Activity.goal_id = :goalId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    suspend fun getSumToGoalInTimeSpan(goalId: Long, from: Calendar, to: Calendar): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(activityEntry: ActivityEntry): Long

    @Delete
    suspend fun delete(activityEntry: ActivityEntry)

    @Query("Delete from ActivityEntry where uid = :activityEntryId")
    suspend fun delete(activityEntryId: Long)

    @Insert
    suspend fun insertData(data: List<ActivityEntry>)

    @Query("Delete from ActivityEntry")
    suspend fun deleteAll()
}