package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


import de.wuapps.moredays.database.entity.Goal;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goal order by order_number asc")
    List<Goal> getAll();

    @Query("SELECT count(*) FROM goal")
    int getCountGoals();

    @Query("SELECT * FROM goal WHERE uid = :goalId")
    Goal loadCategoryById(int goalId);

    @Query("SELECT * FROM goal WHERE name = :name")
    Goal loadCategoryByName(String name);

    @Query("SELECT count(*) from Activity where activity.goal_id =:goalId")
    int isUsed(int goalId);

    @Query("SELECT * FROM goal where is_active>0 order by order_number asc")
    List<Goal> getActiveCategories();

    @Query("SELECT sum(points) FROM goal where is_active> 0")
    int getSumPointsOfActive();

    @Insert(onConflict = REPLACE)
    long insert(Goal goal);

    @Insert
    void insertData(List<Goal> data);

    @Query("Delete from goal")
    void deleteAll();

    @Delete
    void delete(Goal goal);

    @Update
    void update(Goal goal);

    @Update
    void update(List<Goal> goals);
}
