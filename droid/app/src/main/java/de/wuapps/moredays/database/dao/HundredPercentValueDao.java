package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.Date;
import java.util.List;

import de.wuapps.moredays.database.entity.HundredPercentValue;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HundredPercentValueDao {
    @Query("SELECT * FROM hundredpercentvalue order by date desc")
    List<HundredPercentValue> getAll();

    @Query("SELECT points FROM hundredpercentvalue where date = :givenDate")
    int getPointsForGivenDate(String givenDate);

    @Query("SELECT points FROM hundredpercentvalue order by date asc limit 1")
    int getPointsOfFirstEntry();

    @Query("SELECT * FROM hundredpercentvalue where date = :givenDate")
    HundredPercentValue getEntryForGivenDate(String givenDate);

    @Query("SELECT * FROM hundredpercentvalue where date < :givenDate order by date desc limit 1")
    HundredPercentValue getPreviousEntryForGivenDate(String givenDate);

    @Query("SELECT * FROM hundredpercentvalue where date > :givenDate order by date asc limit 1")
    HundredPercentValue getNextEntryForGivenDate(String givenDate);

    @Query("SELECT activeGoals FROM hundredpercentvalue where date = :givenDate")
    String getActiveCategoriesForGivenDate(String givenDate);

    @Insert(onConflict = REPLACE)
    long insert(HundredPercentValue data);

    @Insert
    void insertData(List<HundredPercentValue> data);

    @Query("Delete from hundredpercentvalue")
    void deleteAll();

    @Query("Delete from hundredpercentvalue where date < '191103'")
    void deleteOld();

    @Delete
    void delete(HundredPercentValue data);

    @Update
    void update(HundredPercentValue data);

    @Update
    void update(List<HundredPercentValue> data);
}
