package de.wuapps.moredays.ui.customviews

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import de.wuapps.moredays.R
import java.text.SimpleDateFormat
import java.util.*

class StackedMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val textViewData: TextView
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if (context != null && e.data is GregorianCalendar) {
            val date = e.data as GregorianCalendar
            val formatter = SimpleDateFormat(context.getString(R.string.date_ymd_day))
            textViewData.text = String.format(
                "%s: %.0f",
                formatter.format(date.timeInMillis),
                e.y
            )
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     */
    init {
        textViewData = findViewById(R.id.textViewData)
    }
}