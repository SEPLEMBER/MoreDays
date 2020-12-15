package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Scale

@Dao
interface ScaleDao {
  @get:Query("SELECT * FROM scale order by name")
  val all:List<Scale>
  @Query("SELECT * FROM scale WHERE uid = :scaleId")
  fun getScaleById(scaleId:Int):Scale
  @Insert(onConflict = REPLACE)
  fun insert(scale:Scale):Long
  @Insert
  fun insertData(data:List<Scale>)
  @Query("Delete from scale")
  fun deleteAll()
  @Delete
  fun delete(scale:Scale)
  @Update
  fun update(scale:Scale)
}