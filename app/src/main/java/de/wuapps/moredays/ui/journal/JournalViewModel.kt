package de.wuapps.moredays.ui.journal

import android.content.SharedPreferences
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
import java.util.Calendar
import java.util.Date


class JournalViewModel(private val journalDao: JournalDao, pJournal: Journal?,
                       private val sharedPreferences: SharedPreferences) :
    ViewModel() {
    private var _isNew = pJournal == null
    private val _journal = MutableLiveData<Journal>()
    private var _dateFormatterView = SimpleDateFormat("yy-MM-dd")
    private var _dateFormatDb = Journal.DATE_FORMAT
    val journal: LiveData<Journal>
        get() = _journal
    val hasFab = false
    val hasDeleteOption: Boolean
        get() = !_isNew
    init {
        if (_isNew) {
            iniJournal(Date())
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
        }
    }

    fun changeDate(calendarDate: Calendar){
        val date = DateTypeConverter.convertDateToString(Date(calendarDate.timeInMillis))
        if (date!=journal.value!!.date){
                iniJournal(Date(calendarDate.timeInMillis))
        }
    }

    fun changeMood(mood: Int){
        journal.value!!.mood = mood
    }
    private fun iniJournal(date: Date){
        viewModelScope.launch {
            val dateAsString = DateTypeConverter.convertDateToString(date, _dateFormatDb)
            val tmp = journalDao.getLatestForGivenDate(dateAsString)
            if (tmp==null) {
                val journal = Journal(date, dateAsString)
                journal.note = sharedPreferences.getString("journal_template", "")?:""
                _journal.postValue(journal)
                _isNew = true
            }
            else {
                _isNew = false
                _journal.postValue(tmp)
            }
        }
    }

    fun setDateFormatView(dateFormat: String){
        _dateFormatterView = SimpleDateFormat(dateFormat)
        if (_isNew)
            iniJournal(Date())
    }

    fun setDateFormatDb(dateFormat: String){
        _dateFormatDb = dateFormat
    }

    fun getCurrentDate(): String {
        return if (_journal.value != null)
            _dateFormatterView.format(convertStringToDate(_journal.value!!.date))
        else
            _dateFormatterView.format(Date())
    }
}
