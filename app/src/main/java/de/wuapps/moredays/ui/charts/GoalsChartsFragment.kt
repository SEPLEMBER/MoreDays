package de.wuapps.moredays.ui.charts

import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentGoalsChartsBinding
import de.wuapps.moredays.ui.customviews.SimpleMarkerView
import de.wuapps.moredays.ui.customviews.StackedMarkerView
import de.wuapps.moredays.utilities.DEFAULT_COLOR
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getColorThemeRes
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.floor


class GoalsChartsFragment : Fragment() {

    private val viewModel: GoalsChartsViewModel by viewModels {
        InjectorUtils.provideGoalsChartsViewModelFactory(requireActivity())
    }
    private var _binding: FragmentGoalsChartsBinding? = null
    private val binding get() = _binding!!
    private var _adapter: GoalSummaryAdapter? = null
    private val adapter get() = _adapter!!
    private val BAR_WIDTH = 0.9f
    private val TEXTSIZE_LEGEND = 12f
    private val NUMBER_VISIBLE_BARS = 10
    private var textColorPrimary = DEFAULT_COLOR

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalsChartsBinding.inflate(inflater, container, false)
        val layoutManager = FlexboxLayoutManager(this.requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        binding.recyclerViewAnalysis.layoutManager = layoutManager
        _adapter = GoalSummaryAdapter()
        binding.recyclerViewAnalysis.adapter = adapter
        textColorPrimary = requireContext().getColorThemeRes(android.R.attr.textColorPrimary)
        subscribeUi(adapter)
        return binding.root
    }

    private fun subscribeUi(adapter: GoalSummaryAdapter) {
        viewModel.goalSummaries.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    fillCharts(it)
                    adapter.submitList(it)
                }
            }
        })
        viewModel.goalHistory.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    stackedBarChart(it)
                }
            }
        })
    }

    private fun fillCharts(data: List<GoalsChartsViewModel.GoalSummary>) {
        val barEntriesGoalsTotal: MutableList<BarEntry> = ArrayList()
        val barEntriesGoalsMade: MutableList<BarEntry> = ArrayList()
        val radarEntriesSumPoints: ArrayList<RadarEntry> = ArrayList()
        val radarEntriesGoalsMade: ArrayList<RadarEntry> = ArrayList()
        val barEntriesActivity: MutableList<BarEntry> = ArrayList()
        val colorsActivities: MutableList<Int> = ArrayList()
        val colorsGoals: MutableList<Int> = ArrayList()
        val goalNames: MutableList<String> = ArrayList()
        val activityNames: MutableList<String> = ArrayList()
        for ((i, goalData) in data.withIndex()) {
            colorsGoals.add(goalData.goal.color)
            goalNames.add(goalData.goal.name)
            barEntriesGoalsTotal.add(
                BarEntry(
                    i.toFloat(),
                    goalData.sumPoints.toFloat(),
                    null,
                    goalData.goal.name
                )
            )
            barEntriesGoalsMade.add(
                BarEntry(
                    i.toFloat(),
                    goalData.countDaysMade.toFloat(),
                    null,
                    goalData.goal.name
                )
            )
            radarEntriesSumPoints.add(RadarEntry(goalData.sumPoints.toFloat()))
            radarEntriesGoalsMade.add(RadarEntry(goalData.countDaysMade.toFloat()))
            for ((_, activityEntry) in goalData.activitySummaries.withIndex()) {
                barEntriesActivity.add(
                    BarEntry(
                        activityNames.size.toFloat(),
                        activityEntry.countMade.toFloat(),
                        null,
                        activityEntry.activity.name
                    )
                )
                activityNames.add(activityEntry.activity.name)
                colorsActivities.add(goalData.goal.color)
            }
        }
        adaptBarChart(
            barEntriesActivity,
            binding.barChartActivities,
            colorsActivities,
            activityNames
        )
        binding.barChartActivities.xAxis.setDrawLabels(false)
        if (barEntriesActivity.size > NUMBER_VISIBLE_BARS) {
            binding.barChartActivities.moveViewToX(barEntriesActivity.size.toFloat()) //got to today, calls invalidate already
        } else {
            binding.barChartActivities.invalidate()
        }
        adaptWeb(
            binding.radarChartDaysMade,
            RadarDataSet(radarEntriesGoalsMade, getString(R.string.goal_charts_web_days_made)),
            goalNames.toTypedArray()
        )
    }

    private fun adaptWeb(
        chart: RadarChart,
        radarDataSet: RadarDataSet,
        names: Array<String>
    ) {
        radarDataSet.color = getColor(this.requireContext(), R.color.lime500)
        radarDataSet.fillColor = getColor(this.requireContext(), R.color.gray400)
        radarDataSet.setDrawFilled(true)
        radarDataSet.fillAlpha = 180
        radarDataSet.lineWidth = 2f
        radarDataSet.isDrawHighlightCircleEnabled = true
        radarDataSet.setDrawHighlightIndicators(false)
        val radarDataList: ArrayList<IRadarDataSet> = ArrayList()
        radarDataList.add(radarDataSet)
        chart.description.isEnabled = false
        chart.webLineWidth = 1f
        chart.webColor = Color.LTGRAY
        chart.webLineWidthInner = 1f
        chart.webColorInner = Color.LTGRAY
        chart.webAlpha = 100
        val xAxis: XAxis = chart.xAxis
        xAxis.textSize = TEXTSIZE_LEGEND
        xAxis.textColor = textColorPrimary
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return names[value.toInt() % names.size]
            }
        }
        val yAxis: YAxis = chart.yAxis
        yAxis.setLabelCount(names.size, false)
        yAxis.textSize = TEXTSIZE_LEGEND
        yAxis.textColor = textColorPrimary
        yAxis.axisMinimum = 0f
        yAxis.setDrawLabels(false)
        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.textColor = textColorPrimary
        l.setDrawInside(true)
        val radarData = RadarData(radarDataList)
        radarData.setValueTextSize(TEXTSIZE_LEGEND)
        radarData.setDrawValues(true)
        radarData.setValueTextColor(textColorPrimary)
        radarData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        }
        )
        chart.data = radarData
        chart.animateXY(1000, 1000)
    }

    //return width of first bar
    private fun adaptBarChart(
        entries: List<BarEntry>,
        chart: BarChart,
        colors: List<Int>,
        names: List<String>
    ) {
        val barDataSet = BarDataSet(entries, "")
        barDataSet.colors = colors
        barDataSet.setDrawValues(false)
        val data = BarData(barDataSet)
        data.barWidth = BAR_WIDTH
        chart.data = data
        val barRect = RectF()
        chart.getBarBounds(entries[0], barRect)
        chart.description.isEnabled = false
        chart.setVisibleXRangeMaximum(10f)
        chart.legend.isEnabled = false
        val xAxis: XAxis = chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(names)
        xAxis.setDrawLabels(true)
        xAxis.labelCount = names.size
        xAxis.textSize = TEXTSIZE_LEGEND
        chart.setDrawValueAboveBar(false)
        val intValueFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        }
        val yAxisLeft: YAxis = chart.axisLeft
        val yAxisRight: YAxis = chart.axisRight
        yAxisLeft.axisMinimum = 0f
        yAxisRight.axisMinimum = 0f
        yAxisLeft.valueFormatter = intValueFormatter
        yAxisRight.valueFormatter = intValueFormatter
        yAxisLeft.labelCount = 5
        yAxisRight.labelCount = 5
        val mv = SimpleMarkerView(this.requireContext(), R.layout.simple_marker_view)
        mv.chartView = chart // For bounds control
        chart.setDrawMarkers(true)
        chart.marker = mv // Set the marker to the chart
        chart.isHighlightPerTapEnabled = true //otherwise marker will not work
        val barWidth = barRect.right - barRect.left

        chart.extraBottomOffset = 20F
        val longestLabelLength = Collections.max(
            names,
            Comparator.comparing { obj: String -> obj.length }).length
        if (longestLabelLength * 12 > barWidth) {
            xAxis.labelRotationAngle = 45F
            chart.extraBottomOffset = 50F
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.animateXY(1000, 1000)
    }

    private fun stackedBarChart(data: List<GoalsChartsViewModel.GoalsDate>) {
        if (data.isEmpty()) return //nothing to show
        val countGoals = data[0].goalPoints.size
        val chart = binding.combinedChartHistory
        chart.clear()
        chart.drawOrder = arrayOf(DrawOrder.LINE, DrawOrder.BAR)
        val colors = ArrayList<Int>()
        val labels = Array(countGoals){ ""}
        val barEntries: MutableList<BarEntry> = ArrayList()
        val hundredPercentLineValues = ArrayList<Entry>()
        val shortDateFormat = SimpleDateFormat(getString(R.string.date_md))
        var goalIndex = 0
        var barIndex = 0
        for (goalPoint in data[0].goalPoints) {
            colors.add(goalPoint.first.color)
            labels[goalIndex] = goalPoint.first.name
            goalIndex++
        }
        val dates = arrayOfNulls<String>(data.size + 1)
        for (goalDate in data) {
            val bar = FloatArray(countGoals)
            goalIndex = 0
            for (goalPoint in goalDate.goalPoints) {
                bar[goalIndex] = goalPoint.second.toFloat()
                goalIndex++
            }
            hundredPercentLineValues.add(
                Entry(
                    barIndex.toFloat(),
                    goalDate.hundredPercent.toFloat()
                )
            )
            barEntries.add(BarEntry(barIndex.toFloat(), bar, goalDate.day))
            dates[barIndex] = shortDateFormat.format(goalDate.day.time)
            barIndex++
        }
        //add empty bar to show todays bar full (if there are more than 10 bars)
        if (barIndex > NUMBER_VISIBLE_BARS) {
            dates[barIndex] = ""
            barEntries.add(BarEntry(barIndex.toFloat(), null, ""))
        }
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.setDrawLabels(false)
        chart.axisRight.setDrawLabels(false)

        val mv = StackedMarkerView(this.requireContext(), R.layout.simple_marker_view)
        mv.chartView = chart // For bounds control
        chart.setDrawMarkers(true)
        chart.marker = mv // Set the marker to the chart
        chart.isHighlightPerTapEnabled = true //otherwise marker will not work

        val setLine = LineDataSet(hundredPercentLineValues, "100%")
        setLine.lineWidth = 2f
        setLine.color = Color.RED
        setLine.setDrawCircles(false)
        setLine.enableDashedLine(10f, 5f, 0f)
        setLine.valueTextSize = 0f
        val dataTotalDesired = LineData(setLine)

        val barSet = BarDataSet(barEntries, "")
        barSet.colors = colors
        barSet.stackLabels = labels


        barSet.valueTextSize = 20f

        barSet.setDrawIcons(false)
        val bar = BarData(barSet)
        bar.setDrawValues(false)

        val combinedData = CombinedData()
        combinedData.setData(bar)
        combinedData.setData(dataTotalDesired)
        chart.data = combinedData


        chart.description.isEnabled = false

        chart.setVisibleXRangeMaximum(10f)
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.axisMinimum = 0f

        chart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        }
        chart.axisLeft.labelCount = 5
        chart.axisRight.labelCount = 5

        val l: Legend = chart.legend
        l.textColor = textColorPrimary
        l.isWordWrapEnabled = true

        if (data.size > 10) chart.moveViewToX(data.size.toFloat()) //got to today, calls invalidate already
        else chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
//        binding.combinedChartHistory.data = null
//        binding.barChartActivities.data = null
//        binding.radarChartDaysMade.data = null
        _binding = null

    }
}