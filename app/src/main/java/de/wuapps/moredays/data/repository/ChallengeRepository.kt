package de.wuapps.moredays.data.repository

import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.entity.Challenge
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(private val challengeDao: ChallengeDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allChallenges: Flow<List<Challenge>> = challengeDao.getAll()

    suspend fun insert(challenge: Challenge):Long {
        return challengeDao.insert(challenge)
    }

    suspend fun update(challenge: Challenge){
        challengeDao.update(challenge)
    }

    suspend fun delete(challenge: Challenge) {
        challengeDao.delete(challenge)
    }

        suspend fun getChallengeByIdAndType(id: Long, type: Int): Challenge? {
        return challengeDao.getChallengesByIdAndType(id, type)
    }

}