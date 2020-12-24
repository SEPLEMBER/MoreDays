package de.wuapps.moredays.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.converter.ListGoalTypeConverter
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Activity::class, ActivityEntry::class, Goal::class, Challenge::class, Scale::class, Trophy::class ], version = 4)
@TypeConverters(DateTypeConverter::class)
abstract class MoreDaysDatabase : RoomDatabase() {

    abstract val goalDao: GoalDao

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
                    "moredays_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
