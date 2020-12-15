package de.wuapps.moredays.database.dao

import androidx.room.*
import de.wuapps.moredays.database.entity.Activity

@Dao
interface ActivityDao {
  @get:Query("SELECT activity.uid, activity.goal_id, activity.points, activity.name, activity.is_active FROM Activity order by goal_id asc, activity.points desc")
  val all:List<Activity>
  @get:Query("SELECT activity.uid, activity.goal_id, activity.points, activity.name, activity.is_active FROM Activity inner join goal on goal.uid=activity.goal_id and activity.is_active = 1 order by goal.order_number asc, goal.uid asc, activity.points desc")
  val active:List<Activity>
  @Query("SELECT * FROM Activity WHERE uid = :id")
  fun loadActivityById(id:Int):Activity
  @Query("Select count(*) from Activity where activity.uid = :activityId")
  fun isUsed(activityId:Int):Int
  @Insert
  fun insert(activity:Activity):Long
  @Delete
  fun delete(activity:Activity)
  @Insert
  fun insertData(data:List<Activity>)
  @Query("Delete from Activity")
  fun deleteAll()
  @Update
  fun update(entryType:Activity)
}