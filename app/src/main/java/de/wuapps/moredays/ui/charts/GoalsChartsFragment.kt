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
import com.github.mikephil.charting.charts.CombinedChart
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
import de.wuapps.moredays.databinding.GoalsChartsFragmentBinding
import de.wuapps.moredays.ui.customviews.SimpleMarkerView
import de.wuapps.moredays.utilities.InjectorUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.min

class GoalsChartsFragment : Fragment() {

    private val viewModel: GoalsChartsViewModel by viewModels {
        InjectorUtils.provideGoalsChartsViewModelFactory(requireActivity())
    }
    private var _binding: GoalsChartsFragmentBinding? = null
    private val binding get() = _binding!!
    private var _adapter: GoalSummaryAdapter? = null
    private val adapter get() = _adapter!!
    private val BAR_WIDTH = 0.9f
    private val TEXTSIZE_LEGEND = 12f
    private val NUMBER_VISIBLE_BARS = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GoalsChartsFragmentBinding.inflate(inflater, container, false)
        val layoutManager = FlexboxLayoutManager(this.requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        binding.recyclerViewAnalysis.layoutManager = layoutManager
        _adapter = GoalSummaryAdapter()
        binding.recyclerViewAnalysis.adapter = adapter
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
                    stackedBarChart(binding.combinedChartHistory, it)
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
        var i = 0
        for (goalData in data) {
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
            var j = 0
            for (activityEntry in goalData.activitySummaries) {
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
                j++
            }
            i++
        }
        // barCharts
        adaptBarChart(
            barEntriesGoalsTotal,
            binding.barChartGoalsPointsTotal,
            colorsGoals,
            goalNames
        )
        adaptBarChart(
            barEntriesGoalsMade,
            binding.barChartGoalsDaysMade,
            colorsGoals,
            goalNames
        )
        adaptBarChart(
            barEntriesActivity,
            binding.barChartActivities,
            colorsActivities,
            activityNames
        )
        binding.barChartActivities.getXAxis().setDrawLabels(false)
        if (barEntriesActivity.size > NUMBER_VISIBLE_BARS) {
            binding.barChartActivities.moveViewToX(barEntriesActivity.size.toFloat()) //got to today, calls invalidate already
        } else {
            binding.barChartActivities.invalidate()
        }
        //webs
        adaptWeb(
            binding.radarChartPoints,
            RadarDataSet(radarEntriesSumPoints, "web sum points"),
            goalNames.toTypedArray()
        )
        adaptWeb(
            binding.radarChartDaysMade,
            RadarDataSet(radarEntriesGoalsMade, "web days made"),
            goalNames.toTypedArray()
        )
    }

    private fun adaptWeb(
        chart: RadarChart,
        radarDataSet: RadarDataSet,
        names: Array<String>
    ) {
        radarDataSet.setColor(getColor(this.requireContext(), R.color.secondaryColor))
        radarDataSet.setFillColor(getColor(this.requireContext(), R.color.secondaryVariantColor))
        radarDataSet.setDrawFilled(true)
        radarDataSet.setFillAlpha(180)
        radarDataSet.setLineWidth(2f)
        radarDataSet.setDrawHighlightCircleEnabled(true)
        radarDataSet.setDrawHighlightIndicators(false)
        val radarDataList: ArrayList<IRadarDataSet> = ArrayList<IRadarDataSet>()
        radarDataList.add(radarDataSet)
        chart.getDescription().setEnabled(false)
        chart.setWebLineWidth(1f)
        chart.setWebColor(Color.LTGRAY)
        chart.setWebLineWidthInner(1f)
        chart.setWebColorInner(Color.LTGRAY)
        chart.setWebAlpha(100)
        val xAxis: XAxis = chart.getXAxis()
        xAxis.setTextSize(TEXTSIZE_LEGEND)
        xAxis.setYOffset(0f)
        xAxis.setXOffset(0f)
        xAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return names[value.toInt() % names.size]
            }
        })
        val yAxis: YAxis = chart.getYAxis()
        yAxis.setLabelCount(names.size, false)
        yAxis.setTextSize(TEXTSIZE_LEGEND)
        yAxis.setTextColor(getColor(this.requireContext(), R.color.accentColor))
        yAxis.setAxisMinimum(0f)
        yAxis.setDrawLabels(false)
        val l: Legend = chart.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        l.setDrawInside(true)
        val radarData = RadarData(radarDataList)
        radarData.setValueTextSize(TEXTSIZE_LEGEND)
        radarData.setDrawValues(true)
        radarData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return Math.floor(value.toDouble()).toInt().toString()
            }
        }
        )
        chart.setData(radarData)
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
        barDataSet.setColors(colors)
        barDataSet.setDrawValues(false)
        val data = BarData(barDataSet)
        data.setBarWidth(BAR_WIDTH)
        chart.setData(data)
        val barRect = RectF()
        chart.getBarBounds(entries[0], barRect)
        chart.getDescription().setEnabled(false)
        chart.setVisibleXRangeMaximum(10f)
        chart.getLegend().setEnabled(false)
        val xAxis: XAxis = chart.getXAxis()
        xAxis.setDrawAxisLine(false)
        xAxis.setValueFormatter(IndexAxisValueFormatter(names))
        xAxis.setDrawLabels(true)
        xAxis.setLabelCount(names.size)
        xAxis.setTextSize(TEXTSIZE_LEGEND)
        chart.setDrawValueAboveBar(false)
        val intValueFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return Math.floor(value.toDouble()).toInt().toString()
            }
        }
        val yAxisLeft: YAxis = chart.getAxisLeft()
        val yAxisRight: YAxis = chart.getAxisRight()
        yAxisLeft.setAxisMinimum(0f)
        yAxisRight.setAxisMinimum(0f)
        yAxisLeft.setValueFormatter(intValueFormatter)
        yAxisRight.setValueFormatter(intValueFormatter)
        yAxisLeft.setLabelCount(5)
        yAxisRight.setLabelCount(5)
        val mv = SimpleMarkerView(this.requireContext(), R.layout.simple_marker_view)
        mv.setChartView(chart) // For bounds control
        chart.setDrawMarkers(true)
        chart.setMarker(mv) // Set the marker to the chart
        chart.setHighlightPerTapEnabled(true) //otherwise marker will not work
        val barWidth = barRect.right - barRect.left

        chart.setExtraBottomOffset(20F)
        val longestLabelLength = Collections.max(
            names,
            Comparator.comparing { obj: String -> obj.length }).length
        if (longestLabelLength * 12 > barWidth) {
            xAxis.setLabelRotationAngle(45F)
            chart.setExtraBottomOffset(50F)
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        chart.animateXY(1000, 1000)
    }

    private fun stackedBarChart(chart: CombinedChart, data: List<GoalsChartsViewModel.GoalsDate>) {
        if (data.isEmpty()) return //nothing to show
        val countGoals = data[0].goalPoints.size
        chart.clear()
        chart.setDrawOrder(arrayOf(DrawOrder.LINE, DrawOrder.BAR))
        val colors = ArrayList<Int>()
        val labels = Array<String>(countGoals){ _ -> ""}
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
        chart.getXAxis().setValueFormatter(IndexAxisValueFormatter(dates))
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM)
        chart.getAxisLeft().setDrawLabels(false)
        chart.getAxisRight().setDrawLabels(false)

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
        chart.setData(combinedData)


        chart.getDescription().setEnabled(false)

        chart.setVisibleXRangeMaximum(10f)
        chart.getAxisLeft().setAxisMinimum(0f)
        chart.getAxisRight().setAxisMinimum(0f)

        chart.getAxisLeft().setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return Math.floor(value.toDouble()).toInt().toString()
            }
        })
        chart.getAxisLeft().setLabelCount(5)
        chart.getAxisRight().setLabelCount(5)

        val l: Legend = chart.getLegend()
        l.isWordWrapEnabled = true

        if (data.size > 10) chart.moveViewToX(data.size.toFloat()) //got to today, calls invalidate already
        else chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}