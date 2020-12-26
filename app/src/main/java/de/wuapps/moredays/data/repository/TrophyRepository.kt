package de.wuapps.moredays.data.repository

import de.wuapps.moredays.database.dao.TrophyDao
import de.wuapps.moredays.database.entity.Trophy
import kotlinx.coroutines.flow.Flow

class TrophyRepository(private val trophyDao: TrophyDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTrophys: Flow<List<Trophy>> = trophyDao.getAll()

    suspend fun insert(trophy: Trophy): Long {
        return trophyDao.insert(trophy)
    }

    suspend fun update(trophy: Trophy) {
        trophyDao.update(trophy)
    }

    suspend fun delete(trophy: Trophy) {
        trophyDao.delete(trophy)
    }

}