package de.wuapps.moredays.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.wuapps.moredays.R
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.converter.IntListTypeConverter
import de.wuapps.moredays.database.dao.*
import de.wuapps.moredays.database.entity.*
import de.wuapps.moredays.utilities.DATABASE_NAME
import de.wuapps.moredays.utilities.DATABASE_VERSION
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [Activity::class, ActivityEntry::class, Goal::class, Scale::class, ScaleEntry::class, Trophy::class, Journal::class, HundredPercentValue::class],
    version = DATABASE_VERSION
)
@TypeConverters(DateTypeConverter::class, IntListTypeConverter::class)
abstract class MoreDaysDatabase : RoomDatabase() {

    abstract val goalDao: GoalDao
    abstract val activityDao: ActivityDao
    abstract val activityEntryDao: ActivityEntryDao
    abstract val scaleEntryDao: ScaleEntryDao
    abstract val scaleDao: ScaleDao
    abstract val trophyDao: TrophyDao
    abstract val journalDao: JournalDao
    abstract val hundredPercentValueDao: HundredPercentValueDao

    companion object {
        @Volatile
        private var INSTANCE: MoreDaysDatabase? = null

        private val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Journal ADD COLUMN mood INTEGER NOT NULL DEFAULT -1")
            }
        }

        private val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("Drop table if exists Challenge")
            }
        }
        private val MIGRATION_23_24 = object : Migration(23, 24) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val today = SimpleDateFormat(Journal.DATE_FORMAT).format(Date())
                database.execSQL("insert into HundredPercentValue (date, points) select '$today' as date, sum(points) as points from goal where is_old = 0")
            }
        }

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
                    .addMigrations(MIGRATION_21_22, MIGRATION_22_23, MIGRATION_23_24)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
