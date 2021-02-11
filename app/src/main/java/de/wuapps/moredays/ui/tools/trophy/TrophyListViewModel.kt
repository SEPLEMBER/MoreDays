package de.wuapps.moredays.ui.tools.trophy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.TrophyDao

class TrophyListViewModel(trophyDao: TrophyDao) : ViewModel() {
    val trophies = trophyDao.getAllWithGoal().asLiveData()
    val hasFab = false
}