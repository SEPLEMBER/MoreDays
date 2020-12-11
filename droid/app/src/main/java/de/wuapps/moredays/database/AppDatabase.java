package de.wuapps.moredays.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.wuapps.moredays.database.converter.DateTypeConverter;
import de.wuapps.moredays.database.converter.ListGoalTypeConverter;
import de.wuapps.moredays.database.dao.ActivityDao;
import de.wuapps.moredays.database.dao.ActivityEntryDao;
import de.wuapps.moredays.database.dao.ChallengeDao;
import de.wuapps.moredays.database.dao.GoalDao;
import de.wuapps.moredays.database.dao.HundredPercentValueDao;
import de.wuapps.moredays.database.dao.JournalDao;
import de.wuapps.moredays.database.dao.ScaleDao;
import de.wuapps.moredays.database.dao.ScaleEntryDao;
import de.wuapps.moredays.database.dao.TrophyDao;
import de.wuapps.moredays.database.entity.Activity;
import de.wuapps.moredays.database.entity.ActivityEntry;
import de.wuapps.moredays.database.entity.Challenge;
import de.wuapps.moredays.database.entity.Goal;
import de.wuapps.moredays.database.entity.HundredPercentValue;
import de.wuapps.moredays.database.entity.Journal;
import de.wuapps.moredays.database.entity.Scale;
import de.wuapps.moredays.database.entity.ScaleEntry;
import de.wuapps.moredays.database.entity.Trophy;

import static de.wuapps.moredays.database.DatabaseClient.DATABASE_VERSION;

@Database(entities = {Activity.class, Goal.class, ActivityEntry.class, Scale.class, ScaleEntry.class, Trophy.class, Challenge.class, HundredPercentValue.class, Journal.class}, version = DATABASE_VERSION)
@TypeConverters({DateTypeConverter.class, ListGoalTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActivityDao activityDao();

    public abstract GoalDao goalDao();

    public abstract ActivityEntryDao activityEntryDao();

    public abstract ScaleDao scaleDao();

    public abstract ScaleEntryDao scaleEntryDao();

    public abstract TrophyDao trophyDao();

    public abstract ChallengeDao challengeDao();

    public abstract HundredPercentValueDao hundredPercentValueDao();

    public abstract JournalDao journalDao();
}


