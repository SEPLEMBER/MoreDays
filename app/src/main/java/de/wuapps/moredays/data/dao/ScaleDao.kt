package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.flow.Flow

@Dao
interface ScaleDao {
  @Query("SELECT * FROM scale order by name")
  fun getAll(): Flow<List<Scale>>
  @Query("SELECT * FROM scale WHERE uid = :scaleId")
  suspend fun getScaleById(scaleId:Long):Scale?
  @Query("SELECT count(*) from ScaleEntry where scale_id =:scaleId")
  suspend fun isUsed(scaleId:Long):Int
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