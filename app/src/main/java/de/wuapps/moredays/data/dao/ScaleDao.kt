package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.data.entity.ScaleAndEntries
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.flow.Flow

@Dao
interface ScaleDao {
  @Query("SELECT * FROM scale order by name")
  fun getAll(): Flow<List<Scale>>
  
//  @Transaction
//  @Query("SELECT * FROM scale left join scaleentry on scale.uid = scaleentry.scale_id order by scale.uid, scaleentry.timestamp desc")
//  fun getAllData(): Flow<List<ScaleAndEntries>>

  @Query("SELECT * FROM scale WHERE uid = :scaleId")
  suspend fun getScaleById(scaleId:Long):Scale?
  @Query("SELECT count(*) from ScaleEntry where scale_id =:scaleId")
  suspend fun isUsed(scaleId:Long):Int
  @Insert(onConflict = REPLACE)
  suspend fun insert(scale:Scale):Long
  @Query("Delete from scale")
  suspend fun deleteAll()
  @Delete
  suspend fun delete(scale:Scale)
  @Update
  suspend fun update(scale:Scale)
}