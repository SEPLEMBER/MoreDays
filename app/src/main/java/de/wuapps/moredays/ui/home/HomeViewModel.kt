package de.wuapps.moredays.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.dao.ScaleDao
import kotlinx.coroutines.flow.firstOrNull

class HomeViewModel(private val goalDao: GoalDao, private val scaleDao: ScaleDao, private val challengeDao: ChallengeDao) : ViewModel() {

    val scales = scaleDao.getAll().asLiveData()
    fun allGoalsData() = goalDao.getAllDataForGivenDate()
}