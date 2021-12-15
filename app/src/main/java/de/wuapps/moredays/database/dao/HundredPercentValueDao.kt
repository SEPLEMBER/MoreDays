package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.HundredPercentValue
import kotlinx.coroutines.flow.Flow

@Dao
interface HundredPercentValueDao {
    @Query("SELECT * FROM hundredpercentvalue order by date asc")
    fun getAll(): Flow<List<HundredPercentValue>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(data: HundredPercentValue): Long

    @Insert
    suspend fun insertData(data: List<HundredPercentValue>)

    @Query("Delete from hundredpercentvalue")
    suspend fun deleteAll()

    @Query("Delete from hundredpercentvalue where date < '191103'")
    suspend fun deleteOld()

    @Delete
    suspend fun delete(data: HundredPercentValue)

    @Query("delete from hundredpercentvalue where date = :date")
    suspend fun delete(date: String)

    @Update
    suspend fun update(data: HundredPercentValue)

    @Update
    suspend fun update(data: List<HundredPercentValue>)

}