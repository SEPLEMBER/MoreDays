package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Challenge
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenge order by deadline desc")
    fun getAll(): Flow<List<Challenge>>

    @Query("SELECT * FROM challenge order by deadline desc")
    fun getAllOnce(): List<Challenge>

    @Query("SELECT * FROM challenge WHERE status = :status and date('now') <= date(datetime(deadline / 1000 , 'unixepoch')) order by deadline asc")
    fun getDueByStatus(status: Int): Flow<List<Challenge>>

    @Query("SELECT * FROM challenge WHERE id = :id")
    suspend fun getChallengesById(id: Long): List<Challenge>

    @Query("SELECT * FROM challenge WHERE id = :id and challenge_type = :type")
    suspend fun getChallengesByIdAndType(id: Long, type: Int): List<Challenge>

    @Insert(onConflict = REPLACE)
    suspend fun insert(challenge: Challenge): Long

    @Query("Delete from challenge")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(challenge: Challenge)

    @Update
    suspend fun update(challenge: Challenge)

    @Update
    suspend fun update(challenges: List<Challenge>)
}