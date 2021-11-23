package de.wuapps.moredays.ui.scale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.entity.Scale
import kotlinx.coroutines.launch

class ScaleViewModel(
    private val scaleDao: ScaleDao,
    _scale: Scale?
) : ViewModel() {
    val hasFab = false
    var scale: Scale
    var isNew = _scale == null
    var isUsed = false

    init {
        scale = if (isNew) {
            Scale()
        } else {
            _scale!!
        }
        viewModelScope.launch {
            if (_scale != null)
                isUsed = scaleDao.isUsed(scale.uid) > 0
        }
    }

    fun save() {
        if (isNew) {
            isNew = false
            viewModelScope.launch {
                scale.uid = scaleDao.insert(scale)
            }
        } else {
            viewModelScope.launch {
                scaleDao.update(scale)
            }
        }
    }

    fun delete() = viewModelScope.launch {
        scaleDao.delete(scale)
    }

}