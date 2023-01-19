package de.wuapps.moredays.database.dao

import androidx.room.*
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.flow.Flow

@Dao
interface ScaleDao {
    @Query("SELECT * FROM scale order by name")
    fun getAll(): Flow<List<Scale>>

    @Query("SELECT * FROM scale WHERE uid = :scaleId")
    suspend fun getScaleById(scaleId: Long): Scale?

    @Query("SELECT count(*) from ScaleEntry where scale_id =:scaleId")
    suspend fun isUsed(scaleId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scale: Scale): Long

    @Query("Delete from scale")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(scale: Scale)

    @Update
    suspend fun update(scale: Scale)

}