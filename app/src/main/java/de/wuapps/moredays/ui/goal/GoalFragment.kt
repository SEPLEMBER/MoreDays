package de.wuapps.moredays.ui.goal

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentGoalBinding
import de.wuapps.moredays.ui.activity.ActivityAdapter
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.Integer.parseInt


class GoalFragment : Fragment(), IconDialog.Callback {
    private val SELECT_COLOR_ICON_ID = 502
    private var selectedIconId: Int = 0
    private var selectedColor: Int = 0
    private val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"

    private var _binding: FragmentGoalBinding? = null
    private val binding get() = _binding!!
    private val args: GoalFragmentArgs by navArgs()
    private val viewModel: GoalViewModel by viewModels {
        InjectorUtils.provideGoalViewModelFactory(requireActivity(), args.goal)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.goalViewModel = viewModel
        val iconDialog =
            childFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings())
        binding.imageButtonIcon.setOnClickListener {
            iconDialog.show(
                childFragmentManager,
                ICON_DIALOG_TAG
            )
        }
        selectedIconId = viewModel.goal.symbol
        selectedColor = viewModel.goal.color
        setIcon()
        setColor()

        binding.imageButtonColor.setImageDrawable(
            MoreDaysApplication.iconPack?.let { it.getIcon(SELECT_COLOR_ICON_ID) }?.drawable
        )
        binding.imageButtonColor.setOnClickListener {
            colorPickerDialog()
        }
        binding.imageButtonAddActivity.setOnClickListener {
            if (save()) {
                it.findNavController()
                    .navigate(
                        GoalFragmentDirections.navigationGoalActivity(
                            null,
                            viewModel.goal
                        )
                    )
            }
        }

        if (args.goal != null) {
            val layoutManager = FlexboxLayoutManager(
                requireNotNull(this.activity),
                FlexDirection.ROW
            )
            layoutManager.flexWrap = FlexWrap.WRAP
            binding.recyclerViewActivities.layoutManager = layoutManager

            val adapter = ActivityAdapter(args.goal!!)
            binding.recyclerViewActivities.adapter = adapter
            subscribeUi(adapter)
        }
        handleFab(viewModel.hasFab)
        setHasOptionsMenu(true)
        return view

    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: ActivityAdapter) {
        viewModel.goalAndRelatedActivities.observe(viewLifecycleOwner, {
            it?.let {
                if (it.activities.isNotEmpty())
                    adapter.submitList(it.activities)

            }
        })
    }

    private fun colorPickerDialog() {
        val builder = ColorPickerDialog.Builder(this.activity)
            .setTitle(getString(R.string.title_color_picker_dialog))
            .setPositiveButton(getString(R.string.ok),
                ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color
                    setColor()
                })
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setBottomSpace(12)
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true)
        val colorPickerView = builder.colorPickerView
        val bubbleFlag = BubbleFlag(this.activity)
        bubbleFlag.flagMode = FlagMode.ALWAYS
        colorPickerView.flagView = bubbleFlag
        colorPickerView.setInitialColor(selectedColor)
        builder.show()
    }

    private fun delete() {
        if (!viewModel.isNew) {
            viewModel.delete()
            view?.post {
                findNavController().popBackStack()
            }
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_goal))
            alertDialog.setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                viewModel.delete()
                view?.post {
                    findNavController().popBackStack()
                }

            }
            alertDialog.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    private fun save(): Boolean {
        var isValid = true
        val name = binding.editTextName.text.toString().trim()
        val pointsAsString = binding.editTextPoints.text.toString().trim()
        var points = 0
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (pointsAsString.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        try {
            points = parseInt(pointsAsString)
        } catch (e: NumberFormatException) {
            binding.editTextName.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        if (!isValid)
            return false
        viewModel.goal.points = points //trick supports only one way binding!!!
        viewModel.goal.color = selectedColor
        viewModel.goal.symbol = selectedIconId
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

    private fun setColor() {
        binding.editTextName.setBackgroundColor(selectedColor)
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

    private fun showHelp() {
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.textViewGoal,
                    getString(R.string.title_activities),
                    getString(R.string.help_goal)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.imageButtonAddActivity,
                    getString(R.string.title_activities),
                    getString(R.string.help_activities)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(binding.checkboxIsOld, "", getString(R.string.help_goal_isOld))
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.editTextPoints,
                    getString(R.string.lbl_points),
                    getString(R.string.help_goal_points)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30)
            ).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}