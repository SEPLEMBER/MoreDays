package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
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
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.tools.getFlexboxLayoutManager
import de.wuapps.moredays.ui.tools.isSameDay
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
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
        InjectorUtils.provideHomeViewModelFactory(requireActivity().getApplication())
    }
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _goalAdapter: GoalAdapter? = null
    private val goalAdapter get() = _goalAdapter!!
    private lateinit var challengeAdapter: ChallengeAdapter
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
        challengeAdapter = ChallengeAdapter()
        binding.recyclerviewChallenges.adapter = challengeAdapter
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerviewChallenges.layoutManager = layoutManager
        setFabOnClickListener { showAddOptions() }
        handleLoading()
        setHasOptionsMenu(true)
        if (!preferences.getBoolean(getString(R.string.preferences_showed_first_help), false))
            showFirstHelp()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
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
        binding.datePicker.init(
            iniDate.get(Calendar.YEAR), iniDate.get(Calendar.MONTH),
            iniDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            iniDate.set(Calendar.YEAR, year)
            iniDate.set(Calendar.MONTH, month)
            iniDate.set(Calendar.DAY_OF_MONTH, day)
            viewModel.changeDate(iniDate)
            binding.textViewDate.text = getDateStringToDate(iniDate)
            binding.datePicker.visibility = View.GONE
        }
        binding.datePicker.visibility = View.VISIBLE
    }

    private fun getDateStringToDate(dateCalendar: Calendar): String {
        val today = Calendar.getInstance()
        if (today.isSameDay(dateCalendar))
            return getString(R.string.today)
        return SimpleDateFormat(getString(R.string.date_ymd)).format(dateCalendar.time)
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
            ) { _, which ->
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
        fractionView.value = 0
        val latestValue = viewModel.getLatestValueToScale(scale)
        val decimalValue = Math.floor(latestValue.toDouble()).toInt()
        if (scale.isIntValue) {
            fractionView.visibility = View.GONE
        } else {
            val fractionValues = Array(100) { i -> i.toString() }
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
                binding.circlesView.circlesLabel =
                    String.format(getString(R.string.ini_circle_value), viewModel.name)
                binding.circlesView.setCirclesData(it)

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
        val animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.trophy_dialog
        )
        layoutView.startAnimation(animation)
        lifecycleScope.launch {
            delay(3000)
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
                TapTarget.forView(
                    binding.recyclerviewChallenges,
                    getString(R.string.title_challenge),
                    getString(R.string.help_home_challenges)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
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