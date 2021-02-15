package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.JournalDao

class JournalListViewModelFactory (private val journalDao: JournalDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalListViewModel::class.java)) {
            return JournalListViewModel(journalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
