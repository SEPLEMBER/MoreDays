package de.wuapps.moredays.ui.journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.converter.DateTypeConverter.Companion.convertStringToDate
import de.wuapps.moredays.database.dao.JournalDao
import de.wuapps.moredays.database.entity.Journal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class JournalViewModel(private val journalDao: JournalDao, pJournal: Journal?) :
    ViewModel() {
    private var _isNew = pJournal == null
    private val _journal = MutableLiveData<Journal>()
    private var _dateFormatter = SimpleDateFormat("yy-MM-dd")
    val journal: LiveData<Journal>
        get() = _journal
    val hasFab = false
    val hasDeleteOption: Boolean
        get() = !_isNew
    init {
        if (_isNew) {
            iniJournalWithGivenDate(Date())
        }
        else {
            _journal.value = pJournal!!
        }
    }

    fun save() {
        if (_journal.value != null) {
            if (_isNew) {
                _isNew = false
                viewModelScope.launch {
                    journalDao.insert(_journal.value!!)
                }
            } else {
                viewModelScope.launch {
                    journalDao.update(_journal.value!!)
                }
            }
        }
    }

    fun delete() = viewModelScope.launch {
        if (!_isNew && _journal.value != null) {
            journalDao.delete(_journal.value!!)
            _journal.value = Journal()
        }
        _isNew = true
    }

    fun changeDate(calendarDate: Calendar){
        val date = DateTypeConverter.convertDateToString(Date(calendarDate.timeInMillis))
        if (date!=journal.value!!.date){
                iniJournalWithGivenDate(Date(calendarDate.timeInMillis))
        }
    }

    fun changeMood(mood: Int){
        journal.value!!.mood = mood
    }
    private fun iniJournalWithGivenDate(date: Date){
        viewModelScope.launch {
            val dateAsString = DateTypeConverter.convertDateToString(date)
            val tmp = journalDao.getLatestForGivenDate(dateAsString)
            if (tmp==null) {
                _journal.postValue(Journal(date))
                _isNew = true
            }
            else {
                _isNew = false
                _journal.postValue(tmp!!)
            }
        }
    }

    fun setDateFormat(dateFormat: String){
        _dateFormatter = SimpleDateFormat(dateFormat)
    }

    fun getCurrentDate(): String {
        if (_journal.value != null)
            return _dateFormatter.format(convertStringToDate(_journal.value!!.date))
        else
            return _dateFormatter.format(Date())
    }
}