package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.Journal

@Dao
interface JournalDao {
  @get:Query("SELECT * FROM Journal order by date asc")
  val all:List<Journal>
  @get:Query("SELECT distinct affirmation FROM Journal where affirmation is not null and affirmation != '' order by date asc")
  val affirmations:List<String>
  @get:Query("SELECT count(*) FROM Journal")
  val countJournalEntries:Int
  @Query("SELECT * FROM Journal where date = :givenDate order by date desc LIMIT 1")
  fun getLatestForGivenDate(givenDate:String):Journal
  @Insert(onConflict = REPLACE)
  fun insert(journalEntry:Journal):Long
  @Delete
  fun delete(journalEntry:Journal)
  @Query("Delete from journal")
  fun deleteAll()
  @Update
  fun update(journalEntry:Journal)
}
