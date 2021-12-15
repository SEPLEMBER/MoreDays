package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.utilities.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.text.SimpleDateFormat
import java.util.*


@InternalCoroutinesApi
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireActivity().application)
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _goalAdapter: GoalAdapter? = null
    private val goalAdapter get() = _goalAdapter!!
    private val scales = ArrayList<Scale>()
    private val activityData = ArrayList<Pair<Goal, de.wuapps.moredays.database.entity.Activity>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.textViewDate.text = getString(R.string.today)
        val layoutManagerGoals = LinearLayoutManager(context)
        layoutManagerGoals.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewGoals.layoutManager =
            getFlexboxLayoutManager(requireContext()) //layoutManagerGoals
        val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        viewModel.name = preferences.getString(getString(R.string.preferences_name_key), null) ?: ""
        _goalAdapter = GoalAdapter(viewModel::addGoalEntry)
        binding.recyclerViewGoals.adapter = goalAdapter
        handleLoading()
        setHasOptionsMenu(true)
        if (!preferences.getBoolean(getString(R.string.preferences_showed_first_help), false))
            showFirstHelp()
        setFabOnClickListener { showAddOptions() }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
        val now = Calendar.getInstance()
        if (viewModel.timeLatestActive != null && now.timeInMillis - viewModel.timeLatestActive!!.timeInMillis > 1000*60*1)
            handleDateSelected(now)
    }

    override fun onPause() {
        super.onPause()
        viewModel.timeLatestActive = Calendar.getInstance()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_calendar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                return true
            }
            R.id.menu_date -> {
                showDatePicker()
                return true
            }
            else -> false
        }
    }

    private fun showDatePicker() {
        var iniDate = viewModel.date
        val newFragment = DatePickerFragment(iniDate, this::handleDateSelected)
        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun handleDateSelected(calendar: Calendar){
        viewModel.changeDate(calendar)
        binding.textViewDate.text = getDateStringToDate(calendar)
        if (calendar.isSameDay(Calendar.getInstance()))
            binding.textViewDate.background = null
        else
            binding.textViewDate.setBackgroundColor(getColor(requireContext(), R.color.orange700))
    }

    private fun getDateStringToDate(dateCalendar: Calendar): String {
        val today = Calendar.getInstance()
        if (today.isSameDay(dateCalendar))
            return getString(R.string.today)
        return SimpleDateFormat(getString(R.string.date_ymd_day)).format(dateCalendar.time)
    }

    private fun showAddOptions() {
        val items = ArrayList<String>()
        scales.forEach { scale -> items.add(scale.name) }
        val countScales = scales.size
        val sortedByPoints = activityData.sortedBy {(_, activity) -> activity.points}
        sortedByPoints.forEach { (_, activity) -> items.add(activity.toString()) }
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.add_entry)
            .setItems(
                items.toTypedArray()
            ) { _, which ->
                run {
                    if (which < countScales) {
                        val scale = viewModel.scales.value!!.get(which)
                        showAddScaleEntryDialog(scale)
                    } else {
                        val pair = sortedByPoints.get(which - countScales)
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
        fractionView.value = 0
        val latestValue = viewModel.getLatestValueToScale(scale)
        val decimalValue = Math.floor(latestValue.toDouble()).toInt()
        if (scale.isIntValue) {
            fractionView.visibility = View.GONE
        } else {
            val fractionValues = Array(100) { i -> String.format("%02d", i) }
            val fractionValue = ((latestValue - decimalValue) * 100).toInt()
            fractionView.minValue = 0
            fractionView.maxValue = 99
            fractionView.value = fractionValue
            fractionView.displayedValues = fractionValues
        }
        decimalView.minValue = scale.minValue
        decimalView.maxValue = scale.maxValue
        decimalView.value = decimalValue
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
        viewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                scales.clear()
                scales.addAll(it)
            }
        })
        viewModel.goalEntryList.observe(viewLifecycleOwner, {
            it?.let {
                goalAdapter.submitList(it)
                binding.percentageView.label =  String.format(getString(R.string.ini_circle_value), viewModel.name)
                binding.percentageView.setCirclesData(it)

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

        viewModel.newTrophies.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    if (trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL) {
                        binding.viewKonfetti.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(1000L)
                            .addShapes(Shape.Square, Shape.Circle)
                            .addSizes(Size(12))
                            .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                            .streamFor(300, 2000L)
                    }
                    else {
                        showTrophyDialog(
                            getTrophyMessage(
                                trophy.type,
                                trophy.value,
                                viewModel.latestGoal!!.name
                            )
                        )
                    }
                }
            }
        })
        viewModel.justReached100Percent.observe(viewLifecycleOwner, {
            if (it) {
                binding.lottieTrophy.visibility = View.VISIBLE
                binding.lottieTrophy.playAnimation()
                Handler(Looper.getMainLooper()).postDelayed(({
                    binding.lottieTrophy.visibility = View.GONE

                }), 4000)
            }
            viewModel.justReached100Percent.postValue(false)
        })
    }

    private fun getTrophyMessage(trophyType: Int, value: Int, goalName: String): String {
        when (trophyType) {
            Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW -> return String.format(
                getString(R.string.trophy_type_msg_row),
                viewModel.name,
                goalName,
                value
            )
            Trophy.TROPHY_TYPE_GOAL_TOTAL -> return String.format(
                getString(R.string.trophy_type_msg_total),
                viewModel.name,
                goalName,
                value
            )
            Trophy.TROPHY_TYPE_MADE_GOAL -> return String.format(
                getString(R.string.trophy_type_msg_made_goal),
                viewModel.name,
                goalName
            )
            Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK -> return String.format(
                getString(R.string.trophy_type_msg_times_per_week),
                viewModel.name,
                goalName,
                value
            )
        }
        return ""
    }

    private fun showTrophyDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val layoutView: View = layoutInflater.inflate(R.layout.trophy_custom_dialog, null)
        val textViewMessage = layoutView.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.text = message
        dialogBuilder.setView(layoutView)
        val trophytDialog = dialogBuilder.create()
        trophytDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.trophy_dialog
        )
        layoutView.startAnimation(animation)
        lifecycleScope.launch {
            delay(7000)
            trophytDialog.dismiss()
        }
        trophytDialog.show()

    }

    private fun showFirstHelp() {
        val mainActivity = (activity as MainActivity)
        val bottomNav = mainActivity.findViewById(R.id.nav_view) as BottomNavigationView
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.recyclerViewGoals,
                    getString(R.string.title_goal),
                    getString(R.string.help_home_goal)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(70),
                TapTarget.forView(
                    bottomNav.findViewById(R.id.navigation_tools),
                    getString(R.string.title_tools),
                    getString(R.string.help_nav_tools_first)
                ).cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.textViewHelper,
                    getString(R.string.help),
                    getString(R.string.help_help)
                ).cancelable(false).transparentTarget(true).targetRadius(30)
            )
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                }

                override fun onSequenceFinish() {
                    val preferences: SharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                    preferences.edit().putBoolean(getString(R.string.preferences_showed_first_help), true).apply()
                    Toast.makeText(
                        requireContext(), getString(R.string.msg_tutorial_complete),
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
                }
            }).start()
    }


    private fun showHelp() {
        val mainActivity = (activity as MainActivity)
        val bottomNav = mainActivity.findViewById(R.id.nav_view) as BottomNavigationView
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.recyclerViewGoals,
                    getString(R.string.title_goal),
                    getString(R.string.help_home_goal)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(70),
                TapTarget.forView(mainActivity.getFab(), "+", getString(R.string.help_home_fab))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    bottomNav.findViewById(R.id.navigation_tools),
                    getString(R.string.title_tools), getString(R.string.help_nav_tools)
                ).cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    bottomNav.findViewById(R.id.navigation_charts),
                    getString(R.string.title_chart), getString(R.string.help_nav_charts)
                ).cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    bottomNav.findViewById(R.id.navigation_journalList),
                    getString(R.string.title_journal),
                    getString(R.string.help_journal_intro)
                ).cancelable(false).transparentTarget(true).targetRadius(30)
                    .tintTarget(true)
            ).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _goalAdapter = null
         _binding = null
    }
}