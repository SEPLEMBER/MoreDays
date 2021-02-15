package de.wuapps.moredays.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import de.wuapps.moredays.database.entity.HundredPercentValue

@Dao
interface HundredPercentValueDao {
    @get:Query("SELECT * FROM hundredpercentvalue order by date desc")
    val all: List<HundredPercentValue>

    @get:Query("SELECT points FROM hundredpercentvalue order by date asc limit 1")
    val pointsOfFirstEntry: Int

    @Query("SELECT points FROM hundredpercentvalue where date = :givenDate")
    fun getPointsForGivenDate(givenDate: String): Int

    @Query("SELECT * FROM hundredpercentvalue where date = :givenDate")
    fun getEntryForGivenDate(givenDate: String): HundredPercentValue

    @Query("SELECT * FROM hundredpercentvalue where date < :givenDate order by date desc limit 1")
    fun getPreviousEntryForGivenDate(givenDate: String): HundredPercentValue

    @Query("SELECT * FROM hundredpercentvalue where date > :givenDate order by date asc limit 1")
    fun getNextEntryForGivenDate(givenDate: String): HundredPercentValue

    @Query("SELECT active_goals FROM hundredpercentvalue where date = :givenDate")
    fun getActiveCategoriesForGivenDate(givenDate: String): String

    @Insert(onConflict = REPLACE)
    fun insert(data: HundredPercentValue): Long

    @Insert
    fun insertData(data: List<HundredPercentValue>)

    @Query("Delete from hundredpercentvalue")
    fun deleteAll()

    @Query("Delete from hundredpercentvalue where date < '191103'")
    fun deleteOld()

    @Delete
    fun delete(data: HundredPercentValue)

    @Update
    fun update(data: HundredPercentValue)

    @Update
    fun update(data: List<HundredPercentValue>)
}