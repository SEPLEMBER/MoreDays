package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Goal

@Dao
interface GoalDao {
  @get:Query("SELECT * FROM goal order by order_number asc")
  val all:List<Goal>
  @get:Query("SELECT count(*) FROM goal")
  val countGoals:Int
  @get:Query("SELECT * FROM goal where is_active>0 order by order_number asc")
  val activeCategories:List<Goal>
  @get:Query("SELECT sum(points) FROM goal where is_active> 0")
  val sumPointsOfActive:Int
  @Query("SELECT * FROM goal WHERE uid = :goalId")
  fun loadCategoryById(goalId:Int):Goal
  @Query("SELECT * FROM goal WHERE name = :name")
  fun loadCategoryByName(name:String):Goal
  @Query("SELECT count(*) from Activity where activity.goal_id =:goalId")
  fun isUsed(goalId:Int):Int
  @Insert(onConflict = REPLACE)
  fun insert(goal:Goal):Long
  @Insert
  fun insertData(data:List<Goal>)
  @Query("Delete from goal")
  fun deleteAll()
  @Delete
  fun delete(goal:Goal)
  @Update
  fun update(goal:Goal)
  @Update
  fun update(goals:List<Goal>)
}