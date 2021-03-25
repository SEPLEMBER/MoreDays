package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface TrophyDao {
    @Query("SELECT * FROM Trophy order by timestamp desc")
    fun getAll(): Flow<List<Trophy>>

    @Transaction
    @Query("SELECT * FROM Trophy order by timestamp desc")
    fun getAllWithGoal(): Flow<List<TrophyAndRelatedGoal>>

    @Query("SELECT * FROM Trophy order by timestamp desc")
    suspend fun getAllOnce(): List<Trophy>

    @Query("SELECT count(*) FROM trophy ")
    suspend fun getCountTrophies(): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(item: Trophy): Long

    @Insert
    suspend fun insert(data: List<Trophy>)

    @Query("Delete from Trophy")
    suspend fun deleteAll()

    @Query("Delete from trophy where trophy.uid = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(item: Trophy)

    @Update
    suspend fun update(item: Trophy)

}
