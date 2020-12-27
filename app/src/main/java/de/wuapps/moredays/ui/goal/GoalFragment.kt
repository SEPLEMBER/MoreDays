package de.wuapps.moredays.ui.goal

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.SharedViewModel
import de.wuapps.moredays.ui.SharedViewModelFactory
import de.wuapps.moredays.ui.activity.ActivityAdapter
import java.lang.Integer.parseInt


class GoalFragment : Fragment(), IconDialog.Callback {
    private val TAG = "GoalFragment"
    private val SELECT_COLOR_ICON_ID = 502
    private var _binding: GoalFragmentBinding? = null
    private var selectedIconId: Int = 41
    private var selectedColor: Int = -37722

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: GoalViewModel
    private lateinit var sharedViewModel: SharedViewModel

    //private val viewModel: GoalViewModel by viewModels()
    //val vm: GoalViewModel by lazy { ViewModelProviders.of(this).get(GoalViewModel::class.java) }
    companion object {
        fun newInstance() = GoalFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GoalFragmentBinding.inflate(inflater, container, false)
        var view = binding.root

        val arguments = GoalFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(
                ActivityRepository(db.activityDao),
                GoalRepository(db.goalDao),
                ChallengeRepository(db.challengeDao),
                application
            )
        ).get(SharedViewModel::class.java)
        viewModel = GoalViewModel(GoalRepository(db.goalDao), arguments.goal)
        binding.goalViewModel = viewModel
        binding.setLifecycleOwner(this)
        binding.buttonSave.setOnClickListener({
            save()
            it.findNavController().popBackStack()
        })
        binding.buttonDelete.setOnClickListener({ delete() })
        val iconDialog =
            childFragmentManager.findFragmentByTag(viewModel.ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings())
        binding.imageButtonIcon.setOnClickListener {
            iconDialog.show(
                childFragmentManager,
                viewModel.ICON_DIALOG_TAG
            )
        }
        selectedIconId = viewModel.goal?.symbol ?: -1
        selectedColor = viewModel.goal?.color ?: -1
        setIcon()
        setColor()
        binding.imageButtonColor.setImageDrawable(
            sharedViewModel.iconPack.getIcon(
                SELECT_COLOR_ICON_ID
            )?.drawable
        )
        binding.imageButtonColor.setOnClickListener {
            colorPickerDialog()
        }
        binding.imageButtonAddActivity.setOnClickListener {
            save()
            it.findNavController()
                .navigate(GoalFragmentDirections.navigationGoalActivity(null,
                    viewModel.goal!!
                ))
        }

        if (arguments.goal != null) {
            val layoutManager= FlexboxLayoutManager(
                requireNotNull(this.activity),
                FlexDirection.ROW
            )
            layoutManager.flexWrap = FlexWrap.WRAP
            binding.recyclerViewActivities.layoutManager = layoutManager

            val adapter = ActivityAdapter(arguments.goal)
            binding.recyclerViewActivities.adapter = adapter
            subscribeUi(adapter, binding)
        }
        return view

    }

    private fun subscribeUi(adapter: ActivityAdapter, binding: GoalFragmentBinding) {
        sharedViewModel.activities.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(viewModel.filterLinkedActivites(it) as MutableList<Activity>)

            }
        })
    }

    private fun colorPickerDialog() {
        val builder = ColorPickerDialog.Builder(this.activity)
            .setTitle(getString(R.string.title_color_picker_dialog))
            .setPositiveButton(getString(R.string.ok),
                ColorEnvelopeListener { envelope, fromUser ->
                    selectedColor = envelope.color
                    setColor()
                })
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .setBottomSpace(12)
            .attachAlphaSlideBar(false) // the default value is true.
            .attachBrightnessSlideBar(false)
        val colorPickerView = builder.colorPickerView
        val bubbleFlag = BubbleFlag(this.activity)
        bubbleFlag.flagMode = FlagMode.ALWAYS
        colorPickerView.setFlagView(bubbleFlag)
        colorPickerView.setInitialColor(selectedColor)
        builder.show()
    }

    private fun delete() {
        if (!viewModel.isNew) {
            if (!viewModel.isUsed) {
                viewModel.delete()
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
                findNavController().popBackStack()
            }
            alertDialog.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    private fun save() {
        val name = binding.editTextName.text.toString().trim()
        val pointsAsString = binding.editTextPoints.text.toString().trim()
        var points = 0
        val isOld = binding.checkboxIsOld.isChecked
        if (name.isNullOrBlank())
            binding.editTextName.setError(getString(R.string.warning_field_is_required))
        if (pointsAsString.isNullOrBlank())
            binding.editTextName.setError(getString(R.string.warning_field_is_required))
        try {
            points = parseInt(pointsAsString)
        } catch (e: NumberFormatException) {
            Log.e(TAG, e.message.toString())
        }
        if (viewModel.isNew) {
            var goal = Goal()
            goal.name = name
            goal.points = points
            goal.isOld = isOld
            goal.color = selectedColor
            goal.symbol = selectedIconId
            viewModel.insert(goal)
        } else {
            viewModel.goal!!.points = points //trick supports only one way binding!!!
            viewModel.goal!!.color = selectedColor
            viewModel.goal!!.symbol = selectedIconId
            viewModel.update()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(GoalViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setIcon() {
        if (selectedIconId > 0)
            binding.imageButtonIcon.setImageDrawable(sharedViewModel.iconPack.getIcon(selectedIconId)?.drawable)
    }

    private fun setColor() {
        binding.editTextName.setBackgroundColor(selectedColor)
    }

    /**
     * IconDialog.Callback
     */
    override val iconDialogIconPack: IconPack?
        get() = sharedViewModel.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        if (icons.size > 0) {
            selectedIconId = icons.get(0).id
            setIcon()
        }
    }


}