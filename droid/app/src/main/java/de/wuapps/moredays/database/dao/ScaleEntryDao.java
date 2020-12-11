package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.wuapps.moredays.database.entity.ScaleEntry;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ScaleEntryDao {
    @Query("SELECT * FROM scaleentry")
    List<ScaleEntry> getAll();

    @Query("SELECT value from scaleentry where scale_id = :id order by timestamp desc LIMIT 1")
    float getLatestValue(int id);

    @Query("SELECT *  FROM scaleentry where scale_id = :scaleId order by timestamp ASC ")
    List<ScaleEntry> getAllByScaleId(int scaleId);

    @Insert(onConflict = REPLACE)
    long insert(ScaleEntry entry);

    @Delete
    void delete(ScaleEntry entry);

    @Insert
    void insertData(List<ScaleEntry> data);

    @Query("Delete from scaleentry")
    void deleteAll();

    @Query("Delete from scaleentry where uid = :entryId")
    void delete(int entryId);
}
