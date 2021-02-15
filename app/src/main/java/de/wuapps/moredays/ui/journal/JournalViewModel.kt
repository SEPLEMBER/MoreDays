package de.wuapps.moredays.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.converter.DateTypeConverter.Companion.convertStringToDate
import de.wuapps.moredays.database.dao.JournalDao
import de.wuapps.moredays.database.entity.Journal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class JournalViewModel(private val journalDao: JournalDao, _journal: Journal?) :
    ViewModel() {
    val hasFab = true
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
        if (!isNew) {
            journalDao.delete(journal)
            journal = Journal()
        }
        isNew = true
    }

    fun getFormattedDate(format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(convertStringToDate(journal.date).time)
    }
}