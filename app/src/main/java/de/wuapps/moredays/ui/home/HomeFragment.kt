package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*


@InternalCoroutinesApi
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireActivity().getApplication())
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var challengeAdapter: ChallengeAdapter
    private val scales = ArrayList<Scale>()
    private val activityData = ArrayList<Pair<Goal, de.wuapps.moredays.database.entity.Activity>>()
    private var date = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewGoals.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.entry_type_item_width).toInt()
        )
        goalAdapter = GoalAdapter(viewModel)
        binding.recyclerViewGoals.adapter = goalAdapter
        challengeAdapter = ChallengeAdapter()
        binding.recyclerviewChallenges.adapter = challengeAdapter
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerviewChallenges.layoutManager = layoutManager
        setFabOnClickListener { showAddOptions() }
        subscribeUI()
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
        builder.setTitle(R.string.add_entry)
            .setItems(
                items.toTypedArray()
            ) { dialog, which ->
                run {
                    if (which < countScales) {
                        val scale = viewModel.scales.value!!.get(which)
                        showAddScaleEntryDialog(scale)
                    } else {
                        val pair = activityData.get(which - countScales)
                        viewModel.addGoalEntry(pair.first, pair.second)
                    }
                }
            }
        builder.create().show()
    }

    private fun showAddScaleEntryDialog(scale: Scale) {
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
                viewModel.addScaleEntry(
                    scale.uid,
                    decimalView.value.toFloat() + fractionView.value / 100F
                )
            }
            .setNegativeButton(R.string.cancel, null)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun subscribeUI() {
        viewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                scales.clear()
                scales.addAll(it)
            }
        })
        viewModel.goalEntries.observe(viewLifecycleOwner, {
            it?.let {
                val cardWidthInDp =
                    (getResources().getDimension(R.dimen.home_card_width) / getResources().getDisplayMetrics().density)
                val layoutWidth = binding.layoutHome.width
                if (it.size * (cardWidthInDp + 2) < layoutWidth) {
                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.recyclerViewGoals.layoutManager = layoutManager
                }
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
        viewModel.challenges.observe(viewLifecycleOwner, {
            it?.let {
                val data = it as MutableList<Challenge>
                challengeAdapter.submitList(data)
            }
        })

        viewModel.newTrophies.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    showTrophyDialog(
                        getTrophyMessage(
                            trophy.type,
                            trophy.value,
                            viewModel.latestGoal!!.name
                        ), trophy.type
                    )
                }
            }
        })
        viewModel.justReached100Percent.observe(viewLifecycleOwner, {
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
            viewModel.justReached100Percent.postValue(false)
        })
    }

    private fun getTrophyMessage(trophyType: Int, value: Int, goalName: String): String {
        when (trophyType) {
            Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW -> return String.format(
                getString(R.string.trophy_type_msg_row),
                goalName,
                value
            )
            Trophy.TROPHY_TYPE_GOAL_TOTAL -> return String.format(
                getString(R.string.trophy_type_msg_total),
                goalName,
                value
            )
            Trophy.TROPHY_TYPE_MADE_GOAL -> return String.format(
                getString(R.string.trophy_type_msg_made_goal),
                goalName
            )
            Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK -> return String.format(
                getString(R.string.trophy_type_msg_times_per_week),
                goalName,
                value
            )
        }
        return ""
    }

    private fun showTrophyDialog(message: String, trophyType: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val layoutView: View = layoutInflater.inflate(R.layout.trophy_custom_dialog, null)
        val imageViewTrophy = layoutView.findViewById<ImageView>(R.id.imageViewTrophy)
        imageViewTrophy.setImageDrawable(
            MoreDaysApplication.iconPack?.let { it.getIcon(Trophy.matchTrophyTypeToSymbol(trophyType)) }?.drawable
        )
        val textViewMessage = layoutView.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.setText(message)
        dialogBuilder.setView(layoutView)
        val trophytDialog = dialogBuilder.create()
        trophytDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val animation = AnimationUtils.loadAnimation(requireContext(),
            R.anim.trophy_dialog)
        layoutView.startAnimation(animation)
        lifecycleScope.launch {
            delay(3000)
            trophytDialog.dismiss()
        }
        trophytDialog.show()

    }
}