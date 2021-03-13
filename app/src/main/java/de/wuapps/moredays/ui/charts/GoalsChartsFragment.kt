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

class GoalsChartsFragment : Fragment() {

    private val viewModel: GoalsChartsViewModel by viewModels {
        InjectorUtils.provideGoalsChartsViewModelFactory(requireActivity())
    }
    private lateinit var binding: GoalsChartsFragmentBinding


    private val handleDateFrom = true
    private val dateFormat: SimpleDateFormat? = null
    private val BAR_WIDTH = 0.9f
    private val TEXTSIZE_LEGEND = 12f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GoalsChartsFragmentBinding.inflate(inflater, container, false)
        val layoutManager = FlexboxLayoutManager(this.requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        binding.recyclerViewAnalysis.layoutManager = layoutManager
        val adapter = GoalSummaryAdapter()
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
    }

    private fun fillCharts(data: List<GoalsChartsViewModel.GoalSummary>) {
        val barEntriesGoalsTotal: MutableList<BarEntry> = ArrayList<BarEntry>()
        val barEntriesGoalsMade: MutableList<BarEntry> = ArrayList<BarEntry>()
        val radarEntriesSumPoints: ArrayList<RadarEntry> = ArrayList<RadarEntry>()
        val radarEntriesGoalsMade: ArrayList<RadarEntry> = ArrayList<RadarEntry>()
        val barEntriesActivity: MutableList<BarEntry> = ArrayList<BarEntry>()
        val colorsActivities: MutableList<Int> = ArrayList()
        val colorsGoals: MutableList<Int> = ArrayList()
        val goalNames: MutableList<String> = ArrayList()
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
                        (i + j).toFloat(),
                        activityEntry.countMade.toFloat(),
                        null,
                        activityEntry.activity.name
                    )
                )
                colorsActivities.add(goalData.goal.color)
                j++
            }
            i++
        }
        // barChartGoalsPointsTotal
        var barWidth = adaptBarChart(
            barEntriesGoalsTotal,
            binding.barChartGoalsPointsTotal,
            colorsGoals
        )
        var axis: XAxis = binding.barChartGoalsPointsTotal.getXAxis()
        axis.setValueFormatter(IndexAxisValueFormatter(goalNames))
        axis.setDrawLabels(true)
        axis.setLabelCount(goalNames.size)
        axis.setTextSize(TEXTSIZE_LEGEND)
        binding.barChartGoalsPointsTotal.setExtraBottomOffset(20F) //wegen etwas größerer Schrift, sonst Label unten abgeschnitten
        axis.setPosition(XAxis.XAxisPosition.BOTTOM)
        var longestLabelLength = Collections.max(
            goalNames,
            Comparator.comparing { obj: String -> obj.length }).length
        if (longestLabelLength * 2.8 > barWidth) { //2.8 mit Ausprobieren
            axis.setLabelRotationAngle(45F)
            binding.barChartGoalsPointsTotal.setExtraBottomOffset(50F)
        }
        binding.barChartGoalsPointsTotal.animateXY(1000, 1000)


        //web sum points
        val set1: RadarDataSet
        set1 = RadarDataSet(radarEntriesSumPoints, "web sum points")
        set1.setColor(getColor(this.requireContext(), R.color.secondaryColor))
        set1.setFillColor(getColor(this.requireContext(), R.color.secondaryVariantColor))
        set1.setDrawFilled(true)
        set1.setFillAlpha(180)
        set1.setLineWidth(2f)
        set1.setDrawHighlightCircleEnabled(true)
        set1.setDrawHighlightIndicators(false)
        // web days made
        val set2 = RadarDataSet(radarEntriesGoalsMade, "web days made")
        set1.setColor(getColor(this.requireContext(), R.color.secondaryColor))
        set1.setFillColor(getColor(this.requireContext(), R.color.secondaryVariantColor))
        set2.setDrawFilled(true)
        set2.setFillAlpha(180)
        set2.setLineWidth(2f)
        set2.setDrawHighlightCircleEnabled(true)
        set2.setDrawHighlightIndicators(false)
        val setsPoints: ArrayList<IRadarDataSet> = ArrayList<IRadarDataSet>()
        setsPoints.add(set1)
        val setsMade: ArrayList<IRadarDataSet> = ArrayList<IRadarDataSet>()
        setsMade.add(set2)
        val radarDataPoints = RadarData(setsPoints)
        adaptWeb(binding.radarChartPoints, radarDataPoints, goalNames.toTypedArray())
        val radarDataMade = RadarData(setsMade)
        adaptWeb(binding.radarChartDaysMade, radarDataMade, goalNames.toTypedArray())

        // bar chart goals days made
        barWidth = adaptBarChart(barEntriesGoalsMade, binding.barChartGoalsDaysMade, colorsGoals)
        axis = binding.barChartGoalsDaysMade.getXAxis()
        axis.setValueFormatter(IndexAxisValueFormatter(goalNames))
        axis.setDrawLabels(true)
        axis.setLabelCount(goalNames.size)
        axis.setTextSize(TEXTSIZE_LEGEND)
        binding.barChartGoalsDaysMade.setExtraBottomOffset(20F)
        longestLabelLength = Collections.max(
            goalNames,
            Comparator.comparing { obj: String -> obj.length }).length
        if (longestLabelLength * 2.8 > barWidth) {
            axis.setLabelRotationAngle(45F)
            binding.barChartGoalsDaysMade.setExtraBottomOffset(50F)
        }
        axis.setPosition(XAxis.XAxisPosition.BOTTOM)
        binding.barChartGoalsDaysMade.animateXY(1000, 1000)


        //activity
        adaptBarChart(barEntriesActivity, binding.barChartActivities, colorsActivities)
        if (barEntriesActivity.size > 10) binding.barChartActivities.moveViewToX(barEntriesActivity.size.toFloat()) //got to today, calls invalidate already
        else {
            binding.barChartActivities.invalidate()
        }
    }

    private fun adaptWeb(chart: RadarChart, radarData: RadarData, names: Array<String>) {
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
        radarData.setValueTextSize(TEXTSIZE_LEGEND)
        radarData.setDrawValues(true)
        radarData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return Math.floor(value.toDouble()).toInt().toString()
            }
        }
        )
        chart.setData(radarData)
    }

    //return width of first bar
    private fun adaptBarChart(entries: List<BarEntry>, chart: BarChart, colors: List<Int>): Float {
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
        xAxis.setDrawLabels(false)
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
        return barRect.right - barRect.left
    }
}