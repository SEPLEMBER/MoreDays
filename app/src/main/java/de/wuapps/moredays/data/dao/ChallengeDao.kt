package de.wuapps.moredays.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Challenge

@Dao
interface ChallengeDao {
  @get:Query("SELECT * FROM challenge order by deadline desc")
  val all: List<Challenge>
  @Query("SELECT * FROM challenge WHERE status = :status and date('now') <= date(datetime(deadline / 1000 , 'unixepoch')) order by deadline asc")
  fun getDueByStatus(status:Int):LiveData<List<Challenge>>
  @Query("SELECT * FROM challenge WHERE id = :id")
  fun getChallengesById(id:Int):LiveData<List<Challenge>>
  @Query("SELECT * FROM challenge WHERE id = :id and challenge_type = :type")
  fun getChallengesByIdAndType(id:Int, type:Int):LiveData<List<Challenge>>
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