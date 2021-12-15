package de.wuapps.moredays.utilities

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import de.wuapps.moredays.MainActivity
import java.util.*
import java.util.concurrent.Executors


fun Fragment.handleFab(isFabUsed: Boolean) = if (isFabUsed) showFab() else hideFab()

fun Fragment.showFab() {
    (activity as MainActivity).let { it.showFab() }
}

fun Fragment.hideFab() {
    (activity as MainActivity).let { it.hideFab() }
}

fun Fragment.setFabOnClickListener(l: View.OnClickListener?) {
    (activity as MainActivity).let {
        it.getFab().setOnLongClickListener(null)
        it.getFab().setOnClickListener(l)
    }
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

fun Calendar.isSameDay(date: Calendar): Boolean {
    return date.get(Calendar.YEAR) == this.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == this.get(Calendar.DAY_OF_YEAR)
}
fun Calendar.isSameWeek(date: Calendar):Boolean{
    return date.get(Calendar.YEAR) == this.get(Calendar.YEAR) && date.get(Calendar.WEEK_OF_YEAR) == this.get(Calendar.WEEK_OF_YEAR)
}

fun Calendar.setDate(year: Int, month: Int, day: Int): Calendar {
    this.set(Calendar.YEAR, year)
    this.set(Calendar.MONTH, month)
    this.set(Calendar.DAY_OF_MONTH, day)
    return this
}
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun getFlexboxLayoutManager(context: Context): FlexboxLayoutManager{
    val layoutManager = FlexboxLayoutManager(context)
    layoutManager.flexDirection = FlexDirection.ROW
    layoutManager.flexWrap = FlexWrap.WRAP
    //layoutManager.justifyContent = JustifyContent.SPACE_AROUND
    layoutManager.justifyContent = JustifyContent.CENTER
    return layoutManager
}

@ColorInt
fun Context.getColorThemeRes(@AttrRes id: Int): Int {
    val resolvedAttr = TypedValue()
    this.theme.resolveAttribute(id, resolvedAttr, true)
    return this.getColor(resolvedAttr.resourceId)
}
