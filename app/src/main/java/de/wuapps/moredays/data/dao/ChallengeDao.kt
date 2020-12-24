package de.wuapps.moredays.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Challenge
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
  @Query("SELECT * FROM challenge order by deadline desc")
  fun getAll(): Flow<List<Challenge>>
  @Query("SELECT * FROM challenge WHERE status = :status and date('now') <= date(datetime(deadline / 1000 , 'unixepoch')) order by deadline asc")
  fun getDueByStatus(status:Int):LiveData<List<Challenge>>
  @Query("SELECT * FROM challenge WHERE id = :id")
  suspend fun getChallengesById(id:Long):Challenge?
  @Query("SELECT * FROM challenge WHERE id = :id and challenge_type = :type")
  suspend fun getChallengesByIdAndType(id:Long, type:Int):Challenge?
  @Insert(onConflict = REPLACE)
  fun insert(challenge:Challenge):Long
  @Insert
  fun insertData(data:List<Challenge>)
  @Query("Delete from challenge")
  fun deleteAll()
  @Delete
  fun delete(challenge:Challenge)
  @Update
  fun update(challenge:Challenge)
  @Update
  fun update(challenges:List<Challenge>)
}