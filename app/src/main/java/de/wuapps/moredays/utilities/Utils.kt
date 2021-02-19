package de.wuapps.moredays.ui.tools

import android.view.View
import androidx.fragment.app.Fragment
import de.wuapps.moredays.MainActivity
import java.util.*


fun Fragment.handleFab(isFabUsed: Boolean) = if (isFabUsed) showFab() else hideFab()

fun Fragment.showFab() {
    (activity as MainActivity).let { it.showFab() }
}

fun Fragment.hideFab() {
    (activity as MainActivity).let { it.hideFab() }
}

fun Fragment.setFabOnClickListener(l: View.OnClickListener) {
    (activity as MainActivity).let { it.getFab().setOnClickListener(l) }
}

fun Calendar.atEndOfDay(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 23)
    this.set(Calendar.HOUR, 11)
    this.set(Calendar.MINUTE, 59)
    this.set(Calendar.SECOND, 59)
    return this
}
fun Calendar.atStartOfDay(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.HOUR, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 1)
    return this
}