package de.wuapps.moredays.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.wuapps.moredays.database.entity.Challenge;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChallengeDao {
    @Query("SELECT * FROM challenge order by deadline desc")
    LiveData<List<Challenge>> getAll();

    @Query("SELECT * FROM challenge WHERE status = :status and date('now') <= date(datetime(deadline / 1000 , 'unixepoch')) order by deadline asc")
    LiveData<List<Challenge>> getDueByStatus(int status);

    @Query("SELECT * FROM challenge WHERE id = :id")
    LiveData<List<Challenge>> getChallengesById(int id);

    @Query("SELECT * FROM challenge WHERE id = :id and challenge_type = :type")
    LiveData<List<Challenge>> getChallengesByIdAndType(int id, int type);

    @Insert(onConflict = REPLACE)
    long insert(Challenge challenge);

    @Insert
    void insertData(List<Challenge> data);

    @Query("Delete from challenge")
    void deleteAll();

    @Delete
    void delete(Challenge challenge);

    @Update
    void update(Challenge challenge);

    @Update
    void update(List<Challenge> challenges);
}
