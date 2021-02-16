package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.JournalDao
import de.wuapps.moredays.database.entity.Journal

class JournalViewModelFactory (private val journalDao: JournalDao, private val journal: Journal?) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JournalViewModel(journalDao, journal) as T
    }
}
