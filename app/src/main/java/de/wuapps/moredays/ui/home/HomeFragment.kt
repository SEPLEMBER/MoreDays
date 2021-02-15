package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import kotlinx.coroutines.InternalCoroutinesApi
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*

@InternalCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private lateinit var goalAdapter: GoalAdapter
    private val scales = ArrayList<Scale>()
    private val activityData = ArrayList<Pair<Goal, de.wuapps.moredays.database.entity.Activity>>()
    private var date = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        application = requireNotNull(this.activity).application
        db = MoreDaysDatabase.getInstance(application)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                db.goalDao,
                db.scaleDao,
                db.challengeDao,
                db.activityEntryDao,
                db.scaleEntryDao,
                db.trophyDao
            )
        ).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewGoals.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.entry_type_item_width).toInt()
        )
        goalAdapter = GoalAdapter(homeViewModel)
        binding.recyclerViewGoals.adapter = goalAdapter
        setFabOnClickListener { showAddOptions() }
        handleLoading()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun showAddOptions() {
        val items = ArrayList<String>()
        scales.forEach { scale -> items.add(scale.name) }
        val countScales = scales.size
        activityData.forEach { (_, activity) -> items.add(activity.toString()) }
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.title_affirmation)
            .setItems(items.toTypedArray()
            ) { dialog, which ->
                run{
                    if (which < countScales) {
                        val scale = homeViewModel.scales.value!!.get(which)
                        showAddScaleEntryDialog(scale)
                    } else {
                        val pair = activityData.get(which - countScales)
                        homeViewModel.addGoalEntry(pair.first, pair.second)
                    }
                }
            }
        builder.create().show()
    }

    private fun showAddScaleEntryDialog(scale: Scale){
        val layout =
            LayoutInflater.from(context).inflate(R.layout.float_picker_layout, null)
        val decimalView = layout.findViewById<NumberPicker>(R.id.numberPickerDecimal)
        decimalView.value
        val fractionView = layout.findViewById<NumberPicker>(R.id.numberPickerFraction)
        val fractionValues = Array(100) { i -> i.toString() }
        fractionView.minValue = 0
        fractionView.maxValue = 99
        decimalView.minValue = scale.minValue
        decimalView.maxValue = scale.maxValue
        fractionView.displayedValues = fractionValues
        val dialogBuilder = AlertDialog.Builder(this.activity)
        dialogBuilder
            .setView(layout)
            .setTitle(scale.name)
            .setPositiveButton(R.string.ok) { _, _ ->
                homeViewModel.addScaleEntry(scale.uid, decimalView.value.toFloat() + fractionView.value / 100F)
            }
            .setNegativeButton(R.string.cancel, null)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun handleLoading() {
        if (MoreDaysApplication.loaded.value != null && MoreDaysApplication.loaded.value!!) {
            subscribeUI()
        } else {
            binding.progressBarCyclic.visibility = View.VISIBLE
            MoreDaysApplication.loaded.observe(viewLifecycleOwner, {
                if (it) {
                    subscribeUI()
                }
            })
        }
    }

    private fun subscribeUI() {
        binding.progressBarCyclic.visibility = View.GONE
        homeViewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                scales.clear()
                scales.addAll(it)
            }
        })
        homeViewModel.goalEntries.observe(viewLifecycleOwner, {
            it?.let {
                val data = it as MutableList<GoalEntry>
                goalAdapter.submitList(data)
                binding.circlesView.setCirclesData(data)
                activityData.clear()
                it.forEach { goalEntry ->
                    goalEntry.getActivities().forEach { activity ->
                        activityData.add(
                            Pair(
                                goalEntry.getGoal(),
                                activity
                            )
                        )
                    }
                }
                activityData.toList().sortedBy { (_, activity) -> activity.points }
            }
        })
        homeViewModel.trophies.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    Toast.makeText(application, "trophy", Toast.LENGTH_LONG).show()
                }
            }
        })
        homeViewModel.justReached100Percent.observe(viewLifecycleOwner, {
            if (it) {
                binding.viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12))
                    .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                    .streamFor(300, 5000L)
            }
            homeViewModel.justReached100Percent.postValue(false)
        })
    }

}