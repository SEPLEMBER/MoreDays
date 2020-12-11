package de.wuapps.moredays.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.wuapps.moredays.database.entity.Scale;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ScaleDao {
    @Query("SELECT * FROM scale order by name")
    List<Scale> getAll();

    @Query("SELECT * FROM scale WHERE uid = :scaleId")
    Scale getScaleById(int scaleId);

    @Insert(onConflict = REPLACE)
    long insert(Scale scale);

    @Insert
    void insertData(List<Scale> data);

    @Query("Delete from scale")
    void deleteAll();

    @Delete
    void delete(Scale scale);

    @Update
    void update(Scale scale);
}
