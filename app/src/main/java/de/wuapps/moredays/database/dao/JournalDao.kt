package de.wuapps.moredays.database.dao

import androidx.room.*
import de.wuapps.moredays.database.entity.Journal
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM Journal order by date desc")
    fun getAll(): Flow<List<Journal>>

    @Query("SELECT * FROM Journal where date = :givenDate order by date desc LIMIT 1")
    suspend fun getLatestForGivenDate(givenDate: String): Journal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journalEntry: Journal): Long

    @Delete
    suspend fun delete(journalEntry: Journal)

    @Query("Delete from journal")
    suspend fun deleteAll()

    @Update
    suspend fun update(journalEntry: Journal)
}
