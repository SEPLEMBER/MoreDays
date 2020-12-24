package de.wuapps.moredays.data.repository

import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.flow.Flow

class ScaleRepository(private val scaleDao: ScaleDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allScales: Flow<List<Scale>> = scaleDao.getAll()

    suspend fun insert(scale: Scale):Long {
        return scaleDao.insert(scale)
    }

    suspend fun update(scale: Scale){
        scaleDao.update(scale)
    }

    suspend fun delete(scale: Scale) {
        scaleDao.delete(scale)
    }

    suspend fun isUsed(scaleId: Long):Boolean {
        return scaleDao.isUsed(scaleId) > 0
    }

    suspend fun getScaleById(id: Long): Scale? {
        return scaleDao.getScaleById(id)
    }

}