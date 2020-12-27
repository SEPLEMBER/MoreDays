package de.wuapps.moredays.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.databinding.ActivityFragmentBinding
import de.wuapps.moredays.ui.SharedViewModel
import de.wuapps.moredays.ui.SharedViewModelFactory
import java.lang.Integer.parseInt

class ActivityFragment : Fragment() {
    private val TAG = "ActivityFragment"
    private lateinit var viewModel: ActivityViewModel
    private lateinit var binding: ActivityFragmentBinding

    companion object {
        fun newInstance() = ActivityFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityFragmentBinding.inflate(inflater, container, false)
        var view = binding.root
        val arguments = ActivityFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            this, ActivityViewModelFactory(
                ActivityRepository(MoreDaysDatabase.getInstance(application).activityDao),
                arguments.goal,
                arguments.activity
            )
        ).get(ActivityViewModel::class.java)
        binding.activityViewModel = viewModel

        binding.buttonSave.setOnClickListener({ save() })
        binding.buttonDelete.setOnClickListener({ delete() })
        binding.setLifecycleOwner(this.viewLifecycleOwner)

        return view

    }

    private fun delete() {
        if (!viewModel.isNew) {
            if (!viewModel.isUsed) {
                viewModel.delete()
                return
            }
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_activity))
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
        //val goal = binding.spinnerGoal.tag
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
            var activity = Activity()
            activity.goalId = viewModel.goal.uid
            activity.name = name
            activity.points = points
            activity.isOld = isOld
            viewModel.insert(activity)
        } else {
            viewModel.activity!!.points = points //trick supports only one way binding!!!
            viewModel.activity!!.goalId = viewModel.goal.uid
            viewModel.update()
        }
        findNavController().popBackStack()
    }
}