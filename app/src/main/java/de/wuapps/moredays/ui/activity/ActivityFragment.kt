package de.wuapps.moredays.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.ActivityFragmentBinding
import de.wuapps.moredays.ui.SharedViewModel
import de.wuapps.moredays.ui.SharedViewModelFactory
import java.lang.Integer.parseInt

class ActivityFragment : Fragment() {
    private val TAG = "ActivityFragment"
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityFragmentBinding

//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
    private lateinit var viewModel: ActivityViewModel

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
        sharedViewModel = ViewModelProvider(
            requireActivity(), SharedViewModelFactory(ActivityRepository(MoreDaysDatabase.getInstance(application).activityDao), GoalRepository(MoreDaysDatabase.getInstance(application).goalDao), ChallengeRepository(MoreDaysDatabase.getInstance(application).challengeDao))
        ).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(
            this, ActivityViewModelFactory(
                ActivityRepository(MoreDaysDatabase.getInstance(application).activityDao),
                sharedViewModel.goals,
                arguments.activity
            )
        ).get(ActivityViewModel::class.java)
        binding.activityViewModel = viewModel


        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)

        Log.w("spinner", "end fragment oncreate")
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.buttonSave.setOnClickListener({ save() })
        binding.buttonDelete.setOnClickListener({ delete() })
        Log.w("spinner", "end fragment oncreated")
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
            activity.goalId = viewModel.goal?.uid ?: -1
            activity.name = name
            activity.points = points
            activity.isOld = isOld
            viewModel.insert(activity)
        } else {
            viewModel.activity!!.points = points //trick supports only one way binding!!!
            viewModel.activity!!.goalId = viewModel.goal!!.uid
            viewModel.update()
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

}