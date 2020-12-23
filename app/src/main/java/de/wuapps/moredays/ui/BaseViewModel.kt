package de.wuapps.moredays.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.util.*

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val currentDate: Date = Date()
    val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"
    init {}

}