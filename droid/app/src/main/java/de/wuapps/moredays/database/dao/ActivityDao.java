package de.wuapps.moredays.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.wuapps.moredays.database.entity.Activity;

@Dao
public interface ActivityDao {
    @Query("SELECT activity.uid, activity.goal_id, activity.points, activity.name, activity.is_active FROM Activity order by goal_id asc, activity.points desc")
    List<Activity> getAll();

    @Query("SELECT activity.uid, activity.goal_id, activity.points, activity.name, activity.is_active FROM Activity inner join goal on goal.uid=activity.goal_id and activity.is_active = 1 order by goal.order_number asc, goal.uid asc, activity.points desc")
    List<Activity> getActive();

    @Query("SELECT * FROM Activity WHERE uid = :id")
    Activity loadActivityById(int id);

    @Query("Select count(*) from Activity where activity.uid = :activityId")
    int isUsed(int activityId);

    @Insert
    long insert(Activity activity);

    @Delete
    void delete(Activity activity);

    @Insert
    void insertData(List<Activity> data);

    @Query("Delete from Activity")
    void deleteAll();

    @Update
    void update(Activity entryType);

}
