package de.wuapps.moredays.ui.customviews

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import de.wuapps.moredays.R

class SimpleMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val textViewData: TextView = findViewById(R.id.textViewData)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        textViewData.text = String.format(
            "%s: %.0f",
            e.data.toString(),
            e.y
        ) // set the entry-value as the display text
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}