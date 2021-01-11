package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.wuapps.moredays.database.entity.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ActivityEntryDao {
    @get:Query("SELECT * FROM ActivityEntry order by timestamp desc")
    val all: Flow<List<ActivityEntry>>

    @get:Query("SELECT count(*) FROM ActivityEntry ")
    val countActivityEntries: Int

    @get:Query("SELECT timestamp from ActivityEntry order by timestamp asc limit 1")
    val dateOfFirstActivityEntry: Calendar

    /*@get:Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid order by ActivityEntry.timestamp DESC")
    val allAsSimple:List<SimpleActivity>
    @get:Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    val allAsSimpleForToday:List<SimpleActivity>
    @get:Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    val allAsSimpleForThisWeek:List<SimpleActivity>
    @get:Query("SELECT count(distinct strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')))) FROM ActivityEntry")
    val countDaysTracked:Int
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    fun getAllAsSimpleForGivenDate(date:Date):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByGoal(goalId:Int):List<SimpleActivity>
    @Query("SELECT count(*) FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    fun getCountByGoal(goalId:Int):Int
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByGoalForToday(goalId:Int):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByGoalForGivenDate(goalId:Int, date:Date):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByGoalForThisWeek(goalId:Int):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByActivity(ActivityId:Int):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByActivityForToday(ActivityId:Int):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByActivityForGivenDate(ActivityId:Int, date:Date):List<SimpleActivity>
    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    fun getAsSimpleByActivityForThisWeek(ActivityId:Int):List<SimpleActivity>
    */
    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    fun getSumBygoalIdForGivenDate(goalId: Int, date: Calendar): Int

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    fun getSumForGivenDate(date: Calendar): Int

    //@Query("SELECT sum(Activity.points) as sum, strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) as date, Activity.goal_id as goalId FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId group by strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) order by ActivityEntry.timestamp ASC")
    //fun getSumBygoalIdGroupByDay(goalId:Int):List<SumDate>
    @Query("SELECT count(*) FROM ActivityEntry where ActivityEntry.uid = :ActivityId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    fun getCountToActivityInTimeSpan(ActivityId: Int, from: Calendar, to: Calendar): Int

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    fun getSumToGoalInTimeSpan(goalId: Int, from: Calendar, to: Calendar): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(ActivityEntry: ActivityEntry): Long

    @Delete
    suspend fun delete(ActivityEntry: ActivityEntry)

    @Query("Delete from ActivityEntry where uid = :ActivityEntryId")
    suspend fun delete(ActivityEntryId: Int)

    @Insert
    suspend fun insertData(data: List<ActivityEntry>)

    @Query("Delete from ActivityEntry")
    suspend fun deleteAll()
}