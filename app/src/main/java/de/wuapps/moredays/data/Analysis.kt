package de.wuapps.moredays.data

import de.wuapps.moredays.database.entity.Trophy
import kotlinx.coroutines.delay

class Analysis {

    fun analyzeChallenges(uid: Long){

    }

    suspend fun analyzeTrophies(uid: Long): Trophy? {
        delay(1000)
        return Trophy("test", 1, 1, 1, 1)
    }

}