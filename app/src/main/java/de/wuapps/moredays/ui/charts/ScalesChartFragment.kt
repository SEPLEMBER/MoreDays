package de.wuapps.moredays.ui.charts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.ScaleData
import de.wuapps.moredays.databinding.FragmentScalesChartBinding
import de.wuapps.moredays.utilities.DEFAULT_COLOR
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getColorThemeRes
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

class ScalesChartFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: ScalesChartViewModel by viewModels {
        InjectorUtils.provideScalesChartViewModelFactory(requireActivity())
    }
    private var _binding: FragmentScalesChartBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ScaleEntryAdapter? = null
    private val adapter get() = _adapter!!
    private var selectedScale: Scale? = null
    private val data = ArrayList<ScaleData>()
    private val scaleNames = ArrayList<String>()
    private var textColorPrimary = DEFAULT_COLOR

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScalesChartBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerview.layoutManager = layoutManager
        _adapter = ScaleEntryAdapter()
        binding.recyclerview.adapter = adapter
        val touchHelper = getItemTouchHelper(adapter)
        touchHelper.attachToRecyclerView(binding.recyclerview)
        textColorPrimary = requireContext().getColorThemeRes(android.R.attr.textColorPrimary)
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewModel.scaleData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    data.clear()
                    data.addAll(it)
                    prepareSpinner()
                }
            }
        }
    }

    private fun prepareSpinner() {
        scaleNames.clear()
        for (item in data)
            if (!scaleNames.contains(item.scale.name))
                scaleNames.add(item.scale.name)
        if (scaleNames.size > 1) {
            binding.spinnerScale.visibility = View.VISIBLE
            val adapter = ArrayAdapter(
                this.requireContext(),
                R.layout.spinner_item, scaleNames
            )
            binding.spinnerScale.adapter = adapter
            binding.spinnerScale.onItemSelectedListener = this
        } else {
            binding.spinnerScale.visibility = View.GONE
            if (scaleNames.size == 1) {
                selectedScale = getScaleToName(scaleNames[0])
                fillChart()
            }
        }
    }

    private fun getScaleToName(name: String): Scale? {
        return data.find { it.scale.name == name }?.scale
    }

    private fun fillChart() {
        if (selectedScale == null) return
        var scaleEntries = data.filter { it.scale.uid == selectedScale!!.uid }.map { it.scaleEntry }
        adapter.submitList(scaleEntries)

        val chart = binding.lineChartScale
        scaleEntries = scaleEntries.sortedBy { it.timestamp }
        val mFormat = SimpleDateFormat(getString(R.string.date_md))
        chart.clear()
        chart.visibility = View.VISIBLE
        val listEntries = scaleEntries.map { entry -> Entry(entry.timestamp.time.time.toFloat(), entry.value) }
        val xAxis: XAxis = chart.xAxis
        val lineDataSet = LineDataSet(listEntries, selectedScale!!.name)
        val lineData = LineData(lineDataSet)
        xAxis.valueFormatter = (object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return mFormat.format(Date(value.toLong()))
            }
        })
        xAxis.textColor = textColorPrimary
        val yAxis = chart.axisLeft
        chart.axisRight.isEnabled = false
        if (!selectedScale!!.isIntValue)
            yAxis.enableGridDashedLine(10f, 10f, 0f)
        // axis range
        yAxis.axisMaximum = selectedScale!!.maxValue.toFloat()
        yAxis.axisMinimum = selectedScale!!.minValue.toFloat()
        chart.data = lineData
        yAxis.textColor = textColorPrimary
        val description = Description()
        description.text = selectedScale!!.name
        description.textSize = 14f
        description.textColor = textColorPrimary
        chart.description = description
        chart.invalidate()
    }


    @InternalCoroutinesApi
    private fun getItemTouchHelper(adapter: ScaleEntryAdapter): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val entryId = adapter.getScaleEntryId(viewHolder.absoluteAdapterPosition)
                viewModel.removeScaleEntry(entryId)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.w("trapp", "onDestroyView scaleschartfragment")
        _adapter = null
        _binding = null
    }

    //AdapterView.OnItemSelectedListener
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedScale = getScaleToName(scaleNames[position])
        fillChart()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}