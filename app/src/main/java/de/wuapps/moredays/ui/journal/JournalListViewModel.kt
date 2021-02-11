package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.JournalDao

class JournalListViewModel(journalDao: JournalDao) : ViewModel() {
    // TODO: Implement the ViewModel
    val hasFab = true
    val journalList = journalDao.getAll().asLiveData()
}