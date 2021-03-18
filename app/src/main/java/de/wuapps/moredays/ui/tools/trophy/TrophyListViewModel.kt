package de.wuapps.moredays.ui.tools.trophy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.TrophyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrophyListViewModel(private val trophyDao: TrophyDao) : ViewModel() {
    val trophies = trophyDao.getAllWithGoal().asLiveData()
    val hasFab = false

    fun removeTrophy(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            trophyDao.delete(id)
        }
    }
}