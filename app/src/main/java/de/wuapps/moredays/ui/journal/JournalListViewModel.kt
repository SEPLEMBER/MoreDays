package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.JournalDao
import kotlinx.coroutines.launch

class JournalListViewModel(journalDao: JournalDao) : ViewModel() {
    val hasFab = true
    val journalList = journalDao.getAll().asLiveData()
}