package de.wuapps.moredays.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.wuapps.moredays.database.dao.ChallengeDao

class ChallengeListViewModel (private val challengeDao: ChallengeDao): ViewModel() {
    val goals = challengeDao.getAll().asLiveData()
}