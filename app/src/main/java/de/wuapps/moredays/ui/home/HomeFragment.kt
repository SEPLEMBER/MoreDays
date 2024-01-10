package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.utilities.DatePickerFragment
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getFlexboxLayoutManager
import de.wuapps.moredays.utilities.isSameDay
import de.wuapps.moredays.utilities.setFabOnClickListener
import de.wuapps.moredays.utilities.showFab
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.floor


@InternalCoroutinesApi
class HomeFragment : Fragment(), MenuProvider {

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireActivity().application)
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _goalAdapter: GoalAdapter? = null
    private val goalAdapter get() = _goalAdapter!!
    private val scales = ArrayList<Scale>()
    private val activityData = ArrayList<Pair<Goal, de.wuapps.moredays.database.entity.Activity>>()
    private var preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener() { sharedPreferences: SharedPreferences, _: String? -> handlePreferences(sharedPreferences) }
        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.textViewDate.text = getString(R.string.today)
        val layoutManagerGoals = LinearLayoutManager(context)
        layoutManagerGoals.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewGoals.layoutManager =
            getFlexboxLayoutManager(requireContext()) //layoutManagerGoals
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        handlePreferences(preferences)

        preferences.registerOnSharedPreferenceChangeListener(preferencesListener)
        _goalAdapter = GoalAdapter(viewModel::addGoalEntry, viewModel::deleteLatestGoalEntry)
        binding.recyclerViewGoals.adapter = goalAdapter
        handleLoading()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setFabOnClickListener { showAddOptions() }
            var x1: Float = 0.0F
            var x2: Float = 0.0F
            binding.root.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x1 = event.x
                    }

                    MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX: Float = x2 - x1
                        if (deltaX > 0) {
                            dayBack()
                        } else if (deltaX < 0) {
                            dayForward()
                        }
                    }
                }
                return true
            })
        return binding.root
    }

    private fun handlePreferences(preferences: SharedPreferences) {
        viewModel.name = preferences.getString(getString(R.string.preferences_name_key), null) ?: ""
        if (!preferences.getBoolean(getString(R.string.preferences_showed_first_help), false))
            showFirstHelp()
        viewModel.hideConfetti = preferences.getBoolean(
            getString(R.string.preferences_hide_confetti_key),
            viewModel.hideConfetti
        )
        viewModel.hidePocalAnimation = preferences.getBoolean(
            getString(R.string.preferences_hide_completed_animation_key),
            viewModel.hidePocalAnimation
        )
        viewModel.showProgressAsPercentInCircle = preferences.getBoolean(
            getString(R.string.preferences_show_percentage_key),
            viewModel.showProgressAsPercentInCircle
        )
    }


    override fun onResume() {
        super.onResume()
        showFab()
        val now = Calendar.getInstance()
        if (viewModel.timeLatestActive != null && now.timeInMillis - viewModel.timeLatestActive!!.timeInMillis > 1000 * 60 * 1)
            handleDateSelected(now)
    }

    override fun onPause() {
        super.onPause()
        viewModel.timeLatestActive = Calendar.getInstance()
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_calendar_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
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
        val iniDate = viewModel.date
        val newFragment = DatePickerFragment(iniDate, this::handleDateSelected)
        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun handleDateSelected(calendar: Calendar) {
        viewModel.changeDate(calendar)
        binding.textViewDate.text = getDateStringToDate(calendar)
        if (calendar.isSameDay(Calendar.getInstance()))
            binding.textViewDate.background = null
        else
            binding.textViewDate.setBackgroundColor(getColor(requireContext(), R.color.orange700))
    }

    private fun dayBack(){
        val dayBack = viewModel.date.clone() as Calendar
        dayBack.add(Calendar.DAY_OF_YEAR, -1)
        handleDateSelected(dayBack)
    }
    private fun dayForward(){
        val dayForward = viewModel.date.clone() as Calendar
        dayForward.add(Calendar.DAY_OF_YEAR, 1)
        handleDateSelected(dayForward)
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
        val sortedByPoints = activityData.sortedBy { (_, activity) -> activity.points }
        sortedByPoints.forEach { (_, activity) -> items.add(activity.toString()) }
        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle(R.string.add_entry)
            .setItems(
                items.toTypedArray()
            ) { _, which ->
                run {
                    if (which < countScales) {
                        val scale = viewModel.scales.value!![which]
                        showAddScaleEntryDialog(scale)
                    } else {
                        val pair = sortedByPoints[which - countScales]
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
        val decimalValue = floor(latestValue.toDouble()).toInt()
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
            MoreDaysApplication.loaded.observe(viewLifecycleOwner) {
                if (it) {
                    subscribeUI()
                }
            }
        }
    }

    private fun subscribeUI() {
        binding.progressBarCyclic.visibility = View.GONE
        viewModel.scales.observe(viewLifecycleOwner) {
            it?.let {
                scales.clear()
                scales.addAll(it)
            }
        }
        viewModel.goalEntryList.observe(viewLifecycleOwner) {
            it?.let {
                goalAdapter.submitList(it)
                binding.percentageView.label =
                    String.format(getString(R.string.ini_circle_value), viewModel.name)
                binding.percentageView.setCirclesData(it, viewModel.showProgressAsPercentInCircle)

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
        }

        viewModel.newTrophies.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    if (trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL) {
                        Log.d("prefs", viewModel.hideConfetti.toString())
                        if (!viewModel.hideConfetti && viewModel.confettiShownFor != trophy.uid) {
                            viewModel.confettiShownFor = trophy.uid
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
                    } else {
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
        }
        viewModel.justReached100Percent.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.hidePocalAnimation) {
                    Snackbar.make(
                        requireActivity().findViewById(R.id.container),
                        getString(R.string.trophy_msg_hundred_percent),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    binding.lottieTrophy.visibility = View.VISIBLE
                    binding.lottieTrophy.playAnimation()
                    Handler(Looper.getMainLooper()).postDelayed(({
                        binding.lottieTrophy.visibility = View.GONE

                    }), 3000)
                }
            }
            viewModel.justReached100Percent.postValue(false)
        }
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
        if (viewModel.hidePocalAnimation) {
            Snackbar.make(
                requireActivity().findViewById(R.id.container),
                message,
                Snackbar.LENGTH_LONG
            ).show()
        } else {
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
                delay(6000)
                trophytDialog.dismiss()
            }
            trophytDialog.show()
        }


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
                    preferences.edit()
                        .putBoolean(getString(R.string.preferences_showed_first_help), true).apply()
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
        binding.lottieTrophy.cancelAnimation()
        _goalAdapter = null
        _binding = null
    }
}