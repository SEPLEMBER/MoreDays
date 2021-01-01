package de.wuapps.moredays.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.database.dao.ChallengeDao

class ChallengeListViewModelFactory
    (private val challengeDao: ChallengeDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChallengeListViewModel::class.java)) {
            return ChallengeListViewModel(challengeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}