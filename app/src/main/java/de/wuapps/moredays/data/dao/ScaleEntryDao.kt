package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.wuapps.moredays.database.entity.ScaleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ScaleEntryDao {
    @Query("SELECT * FROM scaleentry")
    fun getAll(): Flow<ScaleEntry>

    @Query("SELECT value from scaleentry where scale_id = :id order by timestamp desc LIMIT 1")
    suspend fun getLatestValue(id: Int): Float

    @Query("SELECT * FROM scaleentry where scale_id = :scaleId order by timestamp ASC ")
    suspend fun getAllByScaleId(scaleId: Int): List<ScaleEntry>

    @Insert(onConflict = REPLACE)
    suspend fun insert(entry: ScaleEntry): Long

    @Delete
    suspend fun delete(entry: ScaleEntry)

    @Insert
    suspend fun insertData(data: List<ScaleEntry>)

    @Query("Delete from scaleentry")
    suspend fun deleteAll()

    @Query("Delete from scaleentry where uid = :entryId")
    suspend fun delete(entryId: Int)
}