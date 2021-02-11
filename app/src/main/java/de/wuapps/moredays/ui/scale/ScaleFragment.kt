package de.wuapps.moredays.ui.scale

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.ScaleFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab
import java.lang.Integer.parseInt

class ScaleFragment : Fragment() {
    private lateinit var viewModel: ScaleViewModel
    private lateinit var binding: ScaleFragmentBinding

    companion object {
        fun newInstance() = ScaleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScaleFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val arguments = ScaleFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            this, ScaleViewModelFactory(
                MoreDaysDatabase.getInstance(application).scaleDao,
                arguments.scale
            )
        ).get(ScaleViewModel::class.java)
        binding.scaleViewModel = viewModel

        binding.buttonSave.setOnClickListener { save() }
        binding.buttonDelete.setOnClickListener { delete() }
        binding.lifecycleOwner = this.viewLifecycleOwner
        handleFab(viewModel.hasFab)
        return view

    }

    private fun delete() {
        if (!viewModel.isNew) {
            if (!viewModel.isUsed) {
                viewModel.delete()
                view?.post {
                    findNavController().popBackStack()
                }
            }
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_scale))
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

    @SuppressLint("RestrictedApi")
    private fun save() {
        var isValid = true
        val name = binding.editTextName.text.toString().trim()
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        val minValueAsString = binding.editTextMinValue.text.toString().trim()
        val maxValueAsString = binding.editTextMaxValue.text.toString().trim()
        var minValue = 0
        var maxValue = 100
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (minValueAsString.isBlank()) {
            binding.editTextMinValue.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (maxValueAsString.isBlank()) {
            binding.editTextMaxValue.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        try {
            minValue = parseInt(minValueAsString)
        } catch (e: NumberFormatException) {
            binding.editTextMinValue.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        try {
            maxValue = parseInt(maxValueAsString)
        } catch (e: NumberFormatException) {
            binding.editTextMaxValue.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        if (!isValid)
            return
        if (viewModel.isNew) {
            val scale = Scale()
            scale.name = name
            scale.minValue = minValue
            scale.maxValue = maxValue
            viewModel.insert(scale)
        } else {
            viewModel.scale!!.minValue = minValue
            viewModel.scale!!.maxValue = maxValue
            viewModel.update()
        }
        view?.post {
            findNavController().popBackStack()
        }
    }
}