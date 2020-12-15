package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.wuapps.moredays.database.entity.ScaleEntry

@Dao
interface ScaleEntryDao {
  @get:Query("SELECT * FROM scaleentry")
  val all:List<ScaleEntry>
  @Query("SELECT value from scaleentry where scale_id = :id order by timestamp desc LIMIT 1")
  fun getLatestValue(id:Int):Float
  @Query("SELECT * FROM scaleentry where scale_id = :scaleId order by timestamp ASC ")
  fun getAllByScaleId(scaleId:Int):List<ScaleEntry>
  @Insert(onConflict = REPLACE)
  fun insert(entry:ScaleEntry):Long
  @Delete
  fun delete(entry:ScaleEntry)
  @Insert
  fun insertData(data:List<ScaleEntry>)
  @Query("Delete from scaleentry")
  fun deleteAll()
  @Query("Delete from scaleentry where uid = :entryId")
  fun delete(entryId:Int)
}