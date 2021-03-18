package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.ActivityEntryAndRelatedData
import de.wuapps.moredays.database.entity.ScaleData
import de.wuapps.moredays.database.entity.ScaleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ScaleEntryDao {
    @Query("SELECT * FROM scaleentry")
    fun getAll(): Flow<List<ScaleEntry>>

    //usage of Transaction is recommended with relations
    @Transaction
    @Query("SELECT * FROM ScaleEntry order by timestamp desc")
    fun getAllScaleData(): Flow<List<ScaleData>>

    @Query("SELECT value from scaleentry where scale_id = :id order by timestamp desc LIMIT 1")
    suspend fun getLatestValue(id: Long): Float

    @Query("SELECT * FROM scaleentry where scale_id = :scaleId order by timestamp ASC ")
    suspend fun getAllByScaleId(scaleId: Long): List<ScaleEntry>

    @Insert(onConflict = REPLACE)
    suspend fun insert(entry: ScaleEntry): Long

    @Delete
    suspend fun delete(entry: ScaleEntry)

    @Insert
    suspend fun insertData(data: List<ScaleEntry>)

    @Query("Delete from scaleentry")
    suspend fun deleteAll()

    @Query("Delete from scaleentry where uid = :entryId")
    suspend fun delete(entryId: Long)
}