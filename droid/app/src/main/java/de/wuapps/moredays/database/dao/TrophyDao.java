package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import de.wuapps.moredays.database.entity.Trophy;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TrophyDao {
    @Query("SELECT * FROM Trophy order by timestamp desc")
    List<Trophy> getAll();

    @Query("SELECT count(*) FROM trophy ")
    int getCountTrophies();

    @Query("SELECT * FROM Trophy where date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
    List<Trophy> getAllForToday();

    @Query("SELECT * FROM Trophy where strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
    List<Trophy> getAllForThisWeek();

    @Query("SELECT * FROM Trophy where goal_id = :goalId order by timestamp desc")
    List<Trophy> getByGoalId(int goalId);

    @Query("SELECT * FROM Trophy where goal_id = :goalId and strftime('%W',date(datetime(trophy.timestamp / 1000 , 'unixepoch'))) = strftime('%W','now') order by timestamp desc")
    List<Trophy> getByGoalIdForThisWeek(int goalId);

    @Query("SELECT * FROM Trophy where goal_id = :goalId and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date('now') order by timestamp desc")
    List<Trophy> getByGoalIdForToday(int goalId);

    @Query("SELECT count(*) from trophy where goal_id = :goalId and trophy.type = :type and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    int getCountByGoalIdAndTypeAndTimespan(int goalId, int type, Date from, Date to);

    @Query("SELECT count(*) from trophy where goal_id = :goalId and datetime(trophy.timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    int getCountByGoalIdAndTimespan(int goalId, Date from, Date to);

    @Query("SELECT count(*) from trophy where uid = :id")
    int  getCountByEntryId(int id);

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
    int getCountToGoalAndType(int goalId, int type);

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(:since / 1000 , 'unixepoch')) <= date(datetime(trophy.timestamp / 1000 , 'unixepoch')) ")
    int getCountToGoalAndTypeSince(int goalId, int type, Date since);

    @Query("SELECT count(*) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type and date(datetime(trophy.timestamp / 1000 , 'unixepoch')) = date(datetime(:date / 1000 , 'unixepoch'))")
    int getCountToGoalAndTypeForGivenDate(int goalId, int type, Date date);


    @Query("SELECT max(trophy.value) FROM Trophy where trophy.goal_id = :goalId and trophy.type = :type")
    int getMaxValueToGoalAndType(int goalId, int type);

    @Query("SELECT count(*) FROM trophy where type= :trophyType and goal_id=:goalId and datetime(timestamp / 1000 , 'unixepoch') between datetime(:from / 1000 , 'unixepoch') and datetime(:to / 1000 , 'unixepoch')")
    int getCountToTimeSpan(int goalId, int trophyType, Date from, Date to);

    @Insert(onConflict = REPLACE)
    long insert(Trophy item);

    @Insert
    void insertData(List<Trophy> data);

    @Query("Delete from Trophy")
    void deleteAll();

    @Query("Delete from trophy where trophy.uid = :id")
    void delete(int id);

    @Delete
    void delete(Trophy item);

    @Update
    void update(Trophy item);

}
