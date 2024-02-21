package de.wuapps.moredays.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.ActivityEntryAndRelatedData
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Dao
interface ActivityEntryDao {
    @Query("SELECT * FROM ActivityEntry order by timestamp desc")
    fun getAll(): Flow<List<ActivityEntry>>

    //usage of Transaction is recommended with relations
    @Transaction
    @Query("SELECT * FROM ActivityEntry order by timestamp desc")
    fun getAllAndRelatedData(): Flow<List<ActivityEntryAndRelatedData>>

    @Query("SELECT count(*) FROM ActivityEntry ")
    suspend fun countActivityEntries(): Int

    @Query("SELECT timestamp from ActivityEntry order by timestamp asc limit 1")
    suspend fun  dateOfFirstActivityEntry(): Calendar

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activityEntry: ActivityEntry): Long

    @Delete
    suspend fun delete(activityEntry: ActivityEntry)

    @Query("Delete from ActivityEntry where uid = :activityEntryId")
    suspend fun delete(activityEntryId: Long)

    @Insert
    suspend fun insertData(data: List<ActivityEntry>)

    @Query("Delete from ActivityEntry")
    suspend fun deleteAll()

    @Query("Delete from ActivityEntry where uid IN (SELECT uid from ActivityEntry WHERE activity_id = :activityId and DATE(timestamp / 1000, 'unixepoch') = DATE(:givenDate / 1000, 'unixepoch') ORDER BY timestamp desc LIMIT 1)")
    suspend fun deleteLatestOfDate(activityId: Long, givenDate: Calendar)
}