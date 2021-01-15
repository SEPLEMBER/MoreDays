package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.JournalDao
import de.wuapps.moredays.database.entity.Journal
import kotlinx.coroutines.launch


class JournalViewModel(private val journalDao: JournalDao, private val _journal: Journal?) :
    ViewModel() {
    var isNew = _journal == null
    var journal: Journal
    init {
        journal = if (isNew) {
            Journal()
        } else {
            _journal!!
        }
    }

    fun save() {
        if (isNew) {
            isNew = false
            viewModelScope.launch {
                journalDao.insert(journal)
            }
        } else {
            viewModelScope.launch {
                journalDao.update(journal)
            }
        }
    }

    fun delete() = viewModelScope.launch {
        journalDao.delete(journal)
        journal = Journal()
        isNew = true
    }
}