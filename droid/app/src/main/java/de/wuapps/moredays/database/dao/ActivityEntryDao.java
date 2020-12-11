package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import de.wuapps.moredays.data.SimpleActivity;
import de.wuapps.moredays.data.SumDate;
import de.wuapps.moredays.database.entity.Activity;
import de.wuapps.moredays.database.entity.ActivityEntry;


import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ActivityEntryDao {
    @Query("SELECT * FROM ActivityEntry order by timestamp desc")
    List<ActivityEntry> getAll();

    @Query("SELECT count(*) FROM ActivityEntry ")
    int getCountActivityEntries();

    @Query("SELECT timestamp from ActivityEntry order by timestamp asc limit 1")
    Date getDateOfFirstActivityEntry();

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAllAsSimple();

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now')  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAllAsSimpleForToday();

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAllAsSimpleForGivenDate(Date date);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid  and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAllAsSimpleForThisWeek();

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByGoal(int goalId);

    @Query("SELECT count(*) FROM ActivityEntry, Activity where ActivityEntry.uid=activity.uid and Activity.goal_id=:goalId order by ActivityEntry.timestamp DESC")
    int getCountByGoal(int goalId);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now')  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByGoalForToday(int goalId);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByGoalForGivenDate(int goalId, Date date);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=Activity.uid and Activity.goal_id=:goalId and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now')  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByGoalForThisWeek(int goalId);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByActivity(int ActivityId);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date('now') order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByActivityForToday(int ActivityId);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch')) order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByActivityForGivenDate(int ActivityId, Date date);

    @Query("SELECT ActivityEntry.timestamp as date, Activity.name, ActivityEntry.uid as uid, Activity.goal_id as goalId, Activity.points as points FROM ActivityEntry, Activity where ActivityEntry.uid=:ActivityId and ActivityEntry.uid=Activity.uid and strftime('%W',date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now')  order by ActivityEntry.timestamp DESC")
    List<SimpleActivity> getAsSimpleByActivityForThisWeek(int ActivityId);

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId and date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    int getSumBygoalIdForGivenDate(int goalId, Date date);

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    int getSumForGivenDate(Date date);

    @Query("SELECT sum(Activity.points) as sum, strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) as date,  Activity.goal_id as goalId FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId group by strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch'))) order by ActivityEntry.timestamp ASC")
    List<SumDate> getSumBygoalIdGroupByDay(int goalId);

    @Query("SELECT count(*) FROM ActivityEntry where ActivityEntry.uid = :ActivityId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    int getCountToActivityInTimeSpan(int ActivityId, Date from, Date to);

    @Query("SELECT sum(Activity.points) as sum FROM ActivityEntry left join Activity on ActivityEntry.uid = Activity.uid where Activity.goal_id = :goalId and datetime(ActivityEntry.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    int getSumToGoalInTimeSpan(int goalId, Date from, Date to);

    @Query("SELECT count(distinct strftime('%d.%m.%Y', date(datetime(ActivityEntry.timestamp / 1000 , 'unixepoch')))) FROM ActivityEntry")
    int getCountDaysTracked();

    @Insert(onConflict = REPLACE)
    long insert(ActivityEntry ActivityEntry);

    @Delete
    void delete(ActivityEntry ActivityEntry);

    @Query("Delete from ActivityEntry where uid = :ActivityEntryId")
    void delete(int ActivityEntryId);

    @Insert
    void insertData(List<ActivityEntry> data);

    @Query("Delete from ActivityEntry")
    void deleteAll();

}
