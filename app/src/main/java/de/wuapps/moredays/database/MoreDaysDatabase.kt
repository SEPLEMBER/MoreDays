package de.wuapps.moredays.database

import android.content.Context
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.wuapps.moredays.R
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.converter.IntListTypeConverter
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.*
import de.wuapps.moredays.ui.tools.ioThread
import de.wuapps.moredays.utilities.DATABASE_NAME
import de.wuapps.moredays.utilities.DATABASE_VERSION
import java.util.*

@Database(
    entities = [Activity::class, ActivityEntry::class, Goal::class, Challenge::class, Scale::class, ScaleEntry::class, Trophy::class, Journal::class, HundredPercentValue::class],
    version = DATABASE_VERSION
)
@TypeConverters(DateTypeConverter::class, IntListTypeConverter::class)
abstract class MoreDaysDatabase : RoomDatabase() {

    abstract val goalDao: GoalDao
    abstract val activityDao: ActivityDao
    abstract val challengeDao: ChallengeDao
    abstract val activityEntryDao: ActivityEntryDao
    abstract val scaleEntryDao: ScaleEntryDao
    abstract val scaleDao: ScaleDao
    abstract val trophyDao: TrophyDao
    abstract val journalDao: JournalDao
    abstract val hundredPercentValueDao: HundredPercentValueDao

    companion object {
        @Volatile
        private var INSTANCE: MoreDaysDatabase? = null

        fun getInstance(
            context: Context,
        ): MoreDaysDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoreDaysDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset(context.getString(R.string.db_ini_name))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
