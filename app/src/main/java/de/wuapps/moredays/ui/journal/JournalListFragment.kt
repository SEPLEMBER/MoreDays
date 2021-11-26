package de.wuapps.moredays.ui.journal

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.emoji2.text.EmojiCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.FragmentJournalListBinding
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.data.PieData
import de.wuapps.moredays.utilities.*


class JournalListFragment : Fragment() {

    private val viewModel: JournalListViewModel by viewModels {
        InjectorUtils.provideJournalListViewModelFactory(requireActivity())
    }
    private var _binding: FragmentJournalListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: JournalAdapter? = null
    private val adapter get() = _adapter!!
    private var isMoodChartVisible = false
    private var textColorPrimary = DEFAULT_COLOR

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJournalListBinding.inflate(inflater, container, false)
        binding.recyclerviewJournal.layoutManager = getFlexboxLayoutManager(requireContext())
        _adapter = JournalAdapter()
        binding.recyclerviewJournal.adapter = adapter
        setFabOnClickListener{
            findNavController().navigate(R.id.navigation_journalList_journal)
        }
        textColorPrimary = requireContext().getColorThemeRes(android.R.attr.textColorPrimary)
        subscribeUi()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi() {
        viewModel.journalList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Journal>)
                binding.textViewToday.text = it.firstOrNull()?.affirmation ?: getString(R.string.label_journal_list_today)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.journallist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_mood_chart -> {
                if (isMoodChartVisible) {
                    if(binding.textViewNoMoodData.visibility == View.VISIBLE)
                        binding.textViewNoMoodData.visibility = View.GONE
                    binding.lineChartMood.visibility = View.GONE
                    binding.pieChartMood.visibility = View.GONE
                }
                else
                    showMoodChart()
                isMoodChartVisible = !isMoodChartVisible
                return true
            }
            else -> false
        }
    }

    private fun showMoodChart(){
        val data = adapter.currentList.filter{it.mood != -1}.sortedBy{it.date}
        if (data.size == 0) {
            binding.textViewNoMoodData.visibility = View.VISIBLE
            return
        }
        val lineChart = binding.lineChartMood
        lineChart.clear()
        lineChart.visibility = View.VISIBLE
        val lineEntries = data.map { journal -> Entry(journal.getDateAsCalendar().time.time.toFloat(),
            journal.mood.toFloat()
        ) }
        val xAxis: XAxis = lineChart.xAxis
        xAxis.textColor = textColorPrimary
        val mFormat = SimpleDateFormat(getString(R.string.date_md))
        xAxis.valueFormatter = (object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return mFormat.format(Date(value.toLong()))
            }
        })
        val yAxis = lineChart.getAxisLeft()
        val lineDataSet = LineDataSet(lineEntries, getString(R.string.mood))
        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setLineWidth(1f)
        lineDataSet.setCircleRadius(3f)
        lineDataSet.setCircleColor(Color.GREEN)
        lineDataSet.setDrawValues(false)
        val lineData = LineData(lineDataSet)
        lineChart.getDescription().setEnabled(false)
        yAxis.setAxisMaximum(5.0f)
        yAxis.setAxisMinimum(0.0f)
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.isEnabled = false
        lineChart.data = lineData
        lineChart.legend.textColor = textColorPrimary
        lineChart.invalidate()

        val pieChart = binding.pieChartMood
        pieChart.clear()
        pieChart.visibility = View.VISIBLE
        val labels = listOf(EmojiCompat.get().process(getString(R.string.mood_face_1)), EmojiCompat.get().process(getString(R.string.mood_face_2)), EmojiCompat.get().process(getString(R.string.mood_face_3)), EmojiCompat.get().process(getString(R.string.mood_face_4)), EmojiCompat.get().process(getString(R.string.mood_face_5)))
        //group.key.toString()
        val pieDataSorted = data.groupBy{it.mood}.toSortedMap()
        val pieEntries = pieDataSorted.map { group -> PieEntry(group.value.size.toFloat() / data.size,
            labels.get(group.key - 1).toString()
        ) }
        val allPieColors = listOf(Color.GRAY, Color.parseColor("#E2DDD9"), Color.parseColor("#0153B6"), Color.parseColor("#9D00F1"), Color.parseColor("#FF8201"))
        val usedPieColors = ArrayList<Int>()
        for(i in 0..allPieColors.size-1){
            if (pieDataSorted.keys.contains(i+1))
                usedPieColors.add(allPieColors.get(i))
        }
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = usedPieColors
        pieDataSet.setDrawValues(false)
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(14f)
        pieChart.data = pieData
        pieChart.getDescription().setEnabled(false)
        pieChart.legend.textSize = 20f
        pieChart.isDrawHoleEnabled = false
        pieChart.invalidate()
    }
}