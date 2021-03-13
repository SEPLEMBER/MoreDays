package de.wuapps.moredays.ui.charts

import androidx.lifecycle.ViewModel
import de.wuapps.moredays.database.dao.ScaleDao
import de.wuapps.moredays.database.dao.ScaleEntryDao

class ScalesChartViewModel(private val scaleDao: ScaleDao, private val scaleEntryDao: ScaleEntryDao) : ViewModel() {

}