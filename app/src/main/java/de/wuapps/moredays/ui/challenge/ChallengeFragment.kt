package de.wuapps.moredays.ui.challenge

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_TYPE_ACTIVITY
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_TYPE_GOAL
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_TYPE_SCALE
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_VALUE_POINTS
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_VALUE_TIMES
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_VALUE_TYPE_LESS
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_VALUE_TYPE_MORE
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentChallengeBinding
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

class ChallengeFragment : Fragment(), IconDialog.Callback {
    private var selectedIconId: Int = 0
    private val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!
    private val args: ChallengeFragmentArgs by navArgs()
    private val viewModel: ChallengeViewModel by viewModels {
        InjectorUtils.provideChallengeViewModelFactory(requireActivity(), args.challenge)
    }
    private lateinit var valueTypesGoal: List<String>
    private lateinit var valueTypesActivity: List<String>
    private lateinit var valueTypesScale: List<String>
    private lateinit var dateFormatter: SimpleDateFormat
    private var challengeType = CHALLENGE_TYPE_GOAL
    private val goals = ArrayList<Goal>()
    private val scales = ArrayList<Scale>()
    private val activities = ArrayList<Activity>()

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dateFormatter = SimpleDateFormat(getString(R.string.date_ymd))
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.challengeViewModel = viewModel
        val iconDialog =
            childFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings())
        binding.imageButtonIcon.setOnClickListener {
            iconDialog.show(
                childFragmentManager,
                ICON_DIALOG_TAG
            )
        }
        selectedIconId = viewModel.challenge.symbol
        setIcon()
        setStartDate(viewModel.challenge.startDate)
        setDeadline(viewModel.challenge.deadline)
        binding.buttonChooseDueDate.setOnClickListener { showDatePicker(true) }
        binding.buttonChooseStartDate.setOnClickListener { showDatePicker(false) }

        valueTypesGoal = listOf(
            getString(R.string.challenge_count_made),
            getString(R.string.challenge_count_points)
        )
        valueTypesActivity = listOf(getString(R.string.challenge_count_made))
        valueTypesScale = listOf("<", ">=")

        binding.rbActivity.setOnClickListener { setChallengeTypeActivity() }
        binding.rbGoal.setOnClickListener { setChallengeTypeGoal() }
        binding.rbScale.setOnClickListener { setChallengeTypeScale() }
        when (viewModel.challenge.challengeType) {
            CHALLENGE_TYPE_ACTIVITY -> {
                setChallengeTypeActivity()
                binding.challengeType.check(binding.rbActivity.id)
            }
            CHALLENGE_TYPE_GOAL -> {
                setChallengeTypeGoal()
                binding.challengeType.check(binding.rbGoal.id)
            }
            CHALLENGE_TYPE_SCALE -> {
                setChallengeTypeScale()
                binding.challengeType.check(binding.rbScale.id)
            }
        }
        when (viewModel.challenge.valueType) {
            CHALLENGE_VALUE_TYPE_LESS -> binding.spinnerValueType.setSelection(0)
            CHALLENGE_VALUE_TYPE_MORE -> binding.spinnerValueType.setSelection(1)
            CHALLENGE_VALUE_TIMES -> binding.spinnerValueType.setSelection(0)
            CHALLENGE_VALUE_POINTS -> binding.spinnerValueType.setSelection(1)
        }
        handleFab(viewModel.hasFab)
        iniSpinner()
        setHasOptionsMenu(true)
        return view

    }

    private fun setChallengeTypeActivity() {
        binding.spinnerScale.visibility = View.GONE
        binding.spinnerGoal.visibility = View.GONE
        binding.spinnerActivity.visibility = View.VISIBLE
        challengeType = CHALLENGE_TYPE_ACTIVITY
        binding.spinnerValueType.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, valueTypesActivity
        )

    }

    private fun setChallengeTypeGoal() {
        binding.spinnerScale.visibility = View.GONE
        binding.spinnerGoal.visibility = View.VISIBLE
        binding.spinnerActivity.visibility = View.GONE
        challengeType = CHALLENGE_TYPE_GOAL
        binding.spinnerValueType.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, valueTypesGoal
        )
    }

    private fun setChallengeTypeScale() {
        binding.spinnerScale.visibility = View.VISIBLE
        binding.spinnerGoal.visibility = View.GONE
        binding.spinnerActivity.visibility = View.GONE
        challengeType = CHALLENGE_TYPE_SCALE
        binding.spinnerValueType.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, valueTypesScale
        )
    }

    private fun iniSpinner() {
        viewModel.goals.observe(viewLifecycleOwner, {
            it?.let {
                if (!it.isEmpty()) {
                    goals.clear()
                    goals.addAll(it)
                    binding.spinnerGoal.adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_item, it
                    )
                }
            }
        })

        viewModel.activities.observe(viewLifecycleOwner, {
            it?.let {
                if (!it.isEmpty()) {
                    activities.clear()
                    activities.addAll(it)
                    binding.spinnerActivity.adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_item, it
                    )
                }
            }
        })
        viewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                if (!it.isEmpty()) {
                    scales.clear()
                    scales.addAll(it)
                    binding.spinnerScale.adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_item, it
                    )
                }
            }
        })


    }

    private fun showDatePicker(isDueDate: Boolean) {
        var iniDate = viewModel.challenge.startDate
        if (isDueDate)
            iniDate = viewModel.challenge.deadline
        binding.datePicker.init(
            iniDate.get(Calendar.YEAR), iniDate.get(Calendar.MONTH),
            iniDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            iniDate.set(Calendar.YEAR, year)
            iniDate.set(Calendar.MONTH, month)
            iniDate.set(Calendar.DAY_OF_MONTH, day)
            if (isDueDate) setDeadline(iniDate)
            else setStartDate(iniDate)
        }
        binding.datePicker.visibility = View.VISIBLE

    }

    private fun getValueType(): Int {
        when (challengeType) {
            CHALLENGE_TYPE_SCALE -> return listOf(
                CHALLENGE_VALUE_TYPE_LESS,
                CHALLENGE_VALUE_TYPE_MORE
            ).get(binding.spinnerValueType.selectedItemPosition)
            CHALLENGE_TYPE_GOAL -> return listOf(CHALLENGE_VALUE_TIMES, CHALLENGE_VALUE_POINTS).get(
                binding.spinnerValueType.selectedItemPosition
            )
            CHALLENGE_TYPE_ACTIVITY -> return CHALLENGE_VALUE_TIMES
        }
        return 0
    }

    private fun getUid(): Long {
        when (challengeType) {
            CHALLENGE_TYPE_SCALE -> return scales.get(binding.spinnerScale.selectedItemPosition).uid
            CHALLENGE_TYPE_GOAL -> return goals.get(binding.spinnerGoal.selectedItemPosition).uid
            CHALLENGE_TYPE_ACTIVITY -> return activities.get(binding.spinnerActivity.selectedItemPosition).uid
        }
        return 0L
    }

    private fun setStartDate(date: Calendar) {
        binding.datePicker.visibility = View.GONE
        binding.buttonChooseStartDate.text = dateFormatter.format(date.time)
        viewModel.challenge.startDate = date
    }

    private fun setDeadline(date: Calendar) {
        binding.datePicker.visibility = View.GONE
        binding.buttonChooseDueDate.text = dateFormatter.format(date.time)
        viewModel.challenge.deadline = date
    }

    private fun delete() {
        viewModel.delete()
        view?.post {
            findNavController().popBackStack()
        }
    }

    private fun save(): Boolean {
        var isValid = true
        if (viewModel.challenge.deadline < viewModel.challenge.startDate) {
            isValid = false
            binding.buttonChooseDueDate.setError(getString(R.string.warning_field_is_required))
        }
        val title = binding.editTextTitle.text.toString().trim()
        val desiredValueAsString = binding.editTextDesiredValue.text.toString().trim()
        var desiredValue = 0
        if (title.isBlank()) {
            binding.editTextTitle.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (desiredValueAsString.isBlank()) {
            binding.editTextDesiredValue.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        try {
            desiredValue = desiredValueAsString.toInt()
        } catch (e: NumberFormatException) {
            binding.editTextDesiredValue.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        if (!isValid)
            return false
        viewModel.challenge.desiredValue = desiredValue //trick supports only one way binding!!!
        viewModel.challenge.symbol = selectedIconId
        viewModel.challenge.valueType = getValueType()
        viewModel.challenge.id = getUid()
        viewModel.save()
        return true
    }

    @InternalCoroutinesApi
    private fun setIcon() {
        if (selectedIconId > 0)
            binding.imageButtonIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(selectedIconId) }?.drawable
            )
    }

    /**
     * IconDialog.Callback
     */
    @InternalCoroutinesApi
    override val iconDialogIconPack: IconPack?
        get() = MoreDaysApplication.iconPack

    @InternalCoroutinesApi
    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        if (icons.isNotEmpty()) {
            selectedIconId = icons[0].id
            setIcon()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                true
            }
            R.id.menu_delete -> {
                delete()
                true
            }
            R.id.menu_save -> {
                if (save())
                    findNavController().popBackStack()
                return true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_delete_save_menu, menu)
        if (viewModel.isNew)
            menu.findItem(R.id.menu_delete).isVisible = false
    }

    private fun showHelp(){
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(binding.textViewTitle, getString(R.string.title_challenges), getString(R.string.help_challenges))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.challengeType, "", getString(R.string.help_add_challenge_type))
                    .cancelable(false).transparentTarget(true).targetRadius(30)
            ).start()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}