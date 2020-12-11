package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import de.wuapps.moredays.database.entity.Journal;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface  JournalDao {
    @Query("SELECT * FROM Journal order by date asc")
    List<Journal> getAll();

    @Query("SELECT distinct affirmation FROM Journal where affirmation is not null and affirmation != '' order by date asc")
    List<String> getAffirmations();

    @Query("SELECT * FROM Journal where date = :givenDate order by date desc LIMIT 1")
    Journal getLatestForGivenDate(String givenDate);

    @Query("SELECT count(*) FROM Journal")
    int getCountJournalEntries();

    @Insert(onConflict = REPLACE)
    long insert(Journal journalEntry);

    @Delete
    void delete(Journal journalEntry);

    @Query("Delete from journal")
    void deleteAll();

    @Update
    void update(Journal journalEntry);
}
