package de.wuapps.moredays.data.repository

import de.wuapps.moredays.database.dao.JournalDao
import de.wuapps.moredays.database.entity.Journal
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allJournals: Flow<List<Journal>> = journalDao.getAll()

    suspend fun insert(journal: Journal): Long {
        return journalDao.insert(journal)
    }

    suspend fun update(journal: Journal) {
        journalDao.update(journal)
    }

    suspend fun delete(journal: Journal) {
        journalDao.delete(journal)
    }

    suspend fun getLatestForGivenDate(date: String): Journal? {
        return journalDao.getLatestForGivenDate(date)
    }
}