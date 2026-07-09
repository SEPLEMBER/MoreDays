package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import de.wuapps.moredays.utilities.cancelFadeOut
import de.wuapps.moredays.utilities.fadeOut
import de.wuapps.moredays.utilities.getFlexboxLayoutManager
import de.wuapps.moredays.utilities.isSameDay
import de.wuapps.moredays.utilities.isToday
import de.wuapps.moredays.utilities.setFabOnClickListener
import de.wuapps.moredays.utilities.showFab
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.floor
//import nl.dionsegijn.konfetti.emitters.StreamEmitter
//import nl.dionsegijn.konfetti.core.Party

@InternalCoroutinesApi
class HomeFragment : Fragment(), MenuProvider {

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireActivity().application)
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _goalAdapter: GoalAdapter? = null
    private val goalAdapter get() = _goalAdapter!!
    private var wasAboveThreshold = false
    private var animateGoalListOnNextUpdate = true
    private var preferences: SharedPreferences? = null
    private val scales = ArrayList<Scale>()
    private val activityData = ArrayList<Pair<Goal, de.wuapps.moredays.database.entity.Activity>>()
    private var preferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener() { sharedPreferences: SharedPreferences, key: String? ->
            handlePreferences(sharedPreferences, key)
        }
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
        binding.recyclerViewGoals.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_goal_cards)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        handlePreferences(preferences!!, null)

        preferences?.registerOnSharedPreferenceChangeListener(preferencesListener)
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

    private fun handlePreferences(preferences: SharedPreferences, key: String?) {
        viewModel.name = preferences.getString(getString(R.string.preferences_name_key), null) ?: ""
        if (!preferences.getBoolean(getString(R.string.preferences_showed_first_help), false))
            showFirstHelp()
        // handle new logic for hide/show stuff
        //val versionCode = BuildConfig.VERSION_CODE
        viewModel.showConfetti = preferences.getBoolean(
            getString(R.string.preferences_show_confetti_key),
            !preferences.getBoolean(
                getString(R.string.preferences_hide_confetti_key), !viewModel.showConfetti)
        )
        viewModel.showPocalAnimation = preferences.getBoolean(
            getString(R.string.preferences_show_completed_animation_key),
            !preferences.getBoolean(getString(R.string.preferences_hide_completed_animation_key),!viewModel.showPocalAnimation)
        )
        viewModel.showProgressAsPercentInCircle = preferences.getBoolean(
            getString(R.string.preferences_show_percentage_key),
            viewModel.showProgressAsPercentInCircle
        )
        if (key == getString(R.string.preferences_affirmation_trigger_key) ||
            key == getString(R.string.preferences_affirmation_threshold_key)
        ) {
            reevaluateThresholdAffirmation()
        }
    }


    override fun onResume() {
        super.onResume()
        showFab()
        val now = Calendar.getInstance()
        if (viewModel.timeLatestActive != null && now.timeInMillis - viewModel.timeLatestActive!!.timeInMillis > (1000 * 60 * 1))
            handleDateSelected(now)
        else
            formatDateTextField()
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
        animateGoalListOnNextUpdate = true
        viewModel.changeDate(calendar)
        formatDateTextField()
    }

    private fun formatDateTextField(){
        binding.textViewDate.text = getDateStringToDate(viewModel.date)
        if (viewModel.date.isSameDay(Calendar.getInstance()))
            binding.textViewDate.background = null
        else
            binding.textViewDate.setBackgroundResource(R.drawable.background_date_badge)
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
            handleAffirmation()
            MoreDaysApplication.loaded.observe(viewLifecycleOwner) {
                if (it) {
                    subscribeUI()
                }
            }
        }
    }
    private fun handleAffirmation() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        val trigger = sharedPref.getString(getString(R.string.preferences_affirmation_trigger_key), "never") ?: "never"
        if (trigger == "start" || trigger == "both") {
            showAffirmation()
        } else {
            binding.textViewAffirmation.cancelFadeOut()
        }
    }

    private fun showAffirmation() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        val sayings = resources.getStringArray(R.array.sayings)
        val random = Calendar.getInstance().timeInMillis % sayings.size
        binding.textViewAffirmation.text = sayings[random.toInt()]
        val duration = sharedPref.getString(getString(R.string.preferences_affirmation_duration_key), "3000")?.toLongOrNull() ?: 3000L
        binding.textViewAffirmation.fadeOut(duration)
    }

    private fun reevaluateThresholdAffirmation() {
        val currentPercent = viewModel.percent.value ?: return
        handleThresholdAffirmation(currentPercent, forceReevaluate = true)
    }

    private fun handleThresholdAffirmation(currentPercent: Float, forceReevaluate: Boolean = false) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        val trigger = sharedPref.getString(getString(R.string.preferences_affirmation_trigger_key), "never") ?: "never"
        if (trigger != "threshold" && trigger != "both") {
            wasAboveThreshold = false
            return
        }

        val threshold = sharedPref.getInt(getString(R.string.preferences_affirmation_threshold_key), 80).toFloat()
        val isAboveThreshold = currentPercent >= threshold
        if (isAboveThreshold && (!wasAboveThreshold || forceReevaluate)) {
            showAffirmation()
        }
        wasAboveThreshold = isAboveThreshold
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
                goalAdapter.submitList(it) {
                    if (animateGoalListOnNextUpdate) {
                        binding.recyclerViewGoals.scheduleLayoutAnimation()
                        animateGoalListOnNextUpdate = false
                    }
                }
                binding.percentageView.label = getString(R.string.ini_circle_value)
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

        viewModel.percent.observe(viewLifecycleOwner) { currentPercent ->
            handleThresholdAffirmation(currentPercent)
        }

        viewModel.newTrophies.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    if (trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL) {
                        if (viewModel.showConfetti && viewModel.confettiShownFor != trophy.goalId) {
                            viewModel.confettiShownFor = trophy.goalId
                            val party = Party(
                                speed = 0f,
                                maxSpeed = 30f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                                position = Position.Relative(0.5, 0.3)
                            )
                            binding.viewKonfetti.start(party)
                        }
                    } else {
                        if(trophy.timestamp.isToday()){
                        showTrophyDialog(
                            getTrophyMessage(
                                trophy.type,
                                trophy.value,
                                viewModel.latestGoal!!.name
                            ))
                        }
                    }
                }
            }
        }
        viewModel.justReached100Percent.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.showPocalAnimation) {
                    binding.lottieTrophy.visibility = View.VISIBLE
                    binding.lottieTrophy.playAnimation()
                    Handler(Looper.getMainLooper()).postDelayed(({
                        binding.lottieTrophy.visibility = View.GONE

                    }), 3000)
                } else {
                    Snackbar.make(
                        requireActivity().findViewById(R.id.container),
                        getString(R.string.trophy_msg_hundred_percent),
                        Snackbar.LENGTH_LONG
                    ).show()
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
        if (viewModel.showPocalAnimation) {
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
        preferences?.unregisterOnSharedPreferenceChangeListener(preferencesListener)
        preferences = null
        binding.textViewAffirmation.cancelFadeOut()
        binding.lottieTrophy.cancelAnimation()
        _goalAdapter = null
        _binding = null
    }
}
