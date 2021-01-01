package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.launch

class ScaleViewModel(private val scaleDao: ScaleDao,
_scale: Scale?
) : ViewModel() {
    val scale = _scale
    val isNew = scale==null
    var isUsed = false

    init {
        viewModelScope.launch {
            if (scale != null)
                isUsed = scaleDao.isUsed(scale.uid) > 0
        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(scale: Scale) = viewModelScope.launch {
        scaleDao.insert(scale)
    }

    fun update() = viewModelScope.launch {
        scaleDao.update(scale!!)
    }

    fun delete() = viewModelScope.launch {
        scaleDao.delete(scale!!)
    }

}