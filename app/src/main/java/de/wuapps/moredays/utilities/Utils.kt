package de.wuapps.moredays.ui.tools

import android.view.View
import androidx.fragment.app.Fragment
import de.wuapps.moredays.MainActivity


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
