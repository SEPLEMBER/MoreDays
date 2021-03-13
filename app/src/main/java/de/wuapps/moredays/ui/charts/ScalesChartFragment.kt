package de.wuapps.moredays.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.ScalesChartFragmentBinding
import de.wuapps.moredays.utilities.InjectorUtils
import java.util.*
import java.util.function.ToDoubleFunction

class ScalesChartFragment : Fragment() {

    private val viewModel: ScalesChartViewModel by viewModels {
        InjectorUtils.provideScaleListViewModelFactory(requireActivity())
    }
    private lateinit var binding: ScalesChartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScalesChartFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    /*private fun fillChart() {
        if (scaleId != null) {
            val appDatabase: AppDatabase = DatabaseClient.getInstance(context).getAppDatabase()
            val selectedScale: Scale = appDatabase.scaleDao().getScaleById(scaleId) ?: return
            chart.clear()
            chart.setVisibility(View.VISIBLE)
            val scaleEntries: List<ScaleEntry> = appDatabase
                .scaleEntryDao()
                .getAllByScaleId(scaleId)
            val scaleDiagramData: ScaleDiagramData
            when (tabIndex) {
                1 -> scaleDiagramData = WeeklyScaleDiagramData(context, scaleEntries)
                2 -> scaleDiagramData = MontlyScaleDiagramData(context, scaleEntries)
                else -> scaleDiagramData = TimestampScaleDiagramData(context, scaleEntries)
            }
            val data: LineData
            val setAvg: LineDataSet
            if (scaleDiagramData.hasDifferentValue()) {
                setAvg = LineDataSet(
                    scaleDiagramData.getAvgValues(),
                    getString(R.string.wu_diagram_label_avg)
                )
                val setMin = LineDataSet(
                    scaleDiagramData.getMinValues(),
                    getString(R.string.wu_diagram_label_min)
                )
                val setMax = LineDataSet(
                    scaleDiagramData.getMaxValues(),
                    getString(R.string.wu_diagram_label_max)
                )
                setMin.lineWidth = 1f
                setMin.enableDashedLine(6f, 5f, 0f)
                setMax.lineWidth = 1f
                setMax.enableDashedLine(6f, 5f, 0f)
                setMin.color = context!!.getColor(R.color.dark_grey)
                setMax.color = context!!.getColor(R.color.dark_grey)
                setMin.setDrawValues(false)
                setMax.setDrawValues(false)
                setMin.setDrawCircles(false)
                setMax.setDrawCircles(false)
                data = LineData(setMin, setAvg, setMax)
            } else {
                setAvg = LineDataSet(
                    scaleDiagramData.getAvgValues(),
                    getString(R.string.wu_diagram_label_values)
                )
                data = LineData(setAvg)
            }
            setAvg.lineWidth = 2f
            setAvg.valueTextSize = 12f
            setAvg.color = context!!.getColor(R.color.primary_dark)
            setAvg.setCircleColor(context!!.getColor(R.color.grey))
            setAvg.setDrawCircleHole(false)
            val xAxis: XAxis = chart.getXAxis()
            xAxis.valueFormatter = scaleDiagramData.getValueFormatter()
            chart.setData(data)
            val description = Description()
            description.text = selectedScale.getName()
            description.textSize = 14f
            chart.setDescription(description)
            chart.invalidate()
        }
    }

    private fun createValues(appDatabase: AppDatabase): ArrayList<Entry>? {
        val values = ArrayList<Entry>()
        val scaleEntries: List<ScaleEntry> = appDatabase
            .scaleEntryDao()
            .getAllByScaleId(scaleId)
        when (tabIndex) {
            1 -> {
                val perWeek: HashMap<Int, List<Float>> =
                    Model.getPreparedScaleData(Calendar.WEEK_OF_YEAR, scaleEntries)
                var i = 0
                while (i < 53) {
                    if (perWeek.containsKey(i)) {
                        val avg = perWeek[i]!!
                            .stream().mapToDouble(ToDoubleFunction { v: Float? -> v }).average()
                            .orElse(0.0)
                        values.add(Entry(i.toFloat(), avg.toFloat()))
                    }
                    i++
                }
            }
            2 -> {
                val perMonth: HashMap<Int, List<Float>> =
                    Model.getPreparedScaleData(Calendar.MONTH, scaleEntries)
                var i = 0
                while (i < 13) {
                    if (perMonth.containsKey(i)) {
                        val avg = perMonth[i]!!
                            .stream().mapToDouble(ToDoubleFunction { v: Float? -> v }).average()
                            .orElse(0.0)
                        values.add(Entry(i.toFloat(), avg.toFloat()))
                    }
                    i++
                }
            }
            else -> for (se in scaleEntries) {
                values.add(Entry(se.getTimestamp().getTime(), se.getValue()))
            }
        }
        return values
    }
*/

}